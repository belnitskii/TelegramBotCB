package com.belnitskii.telegrambotcb.service;

import com.belnitskii.telegrambotcb.config.ApiUrls;
import com.belnitskii.telegrambotcb.constant.ValutaCharCode;
import com.belnitskii.telegrambotcb.model.Record;
import com.belnitskii.telegrambotcb.model.ValCurs;
import com.belnitskii.telegrambotcb.model.Valuta;
import com.belnitskii.telegrambotcb.util.DateTimeUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.belnitskii.telegrambotcb.util.ChartUtil.generateChart;

/**
 * Сервис для работы с валютными курсами.
 * Содержит методы для получения курса валюты, курса валюты за неделю и построения графика курса валюты.
 * Работает с API Центробанка России.
 */
@Service
public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private static final String TABLE_HEADER = "<code> " +
            String.format("%-6s", "📅") +  // Эмодзи + 1 пробел
            "|    " +
            String.format("%-11s", "💰 EUR") + // Эмодзи + EUR + 2 пробела
            "| " +
            String.format("%-6s", "  📈Δ") + // Эмодзи + Δ + 1 пробел
            "</code>\n";

    /**
     * Получает текущий курс валюты по её символу (charCode) и возвращает строку с курсом.
     *
     * @param charCode Символ валюты (например, USD, EUR).
     * @return Строка с курсом валюты или {@code null} в случае ошибки.
     * @throws ParseException Если возникла ошибка при парсинге данных.
     */
    public String getLatestRates(String charCode) {
        List<Record> recordList = fetchRecordsFromNow(charCode, 2);
        LocalDate lastDateRecord = LocalDate.parse(recordList.getLast().getDate(), DateTimeUtil.FORMATTER);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        //Если в XML есть курс на завтра — возвращаем курс за два дня (сегодня + завтра)
        if (tomorrow.isEqual(lastDateRecord)) {
            return getRatesForPeriod(charCode, 2);
        }

        //Проверяем JSON, если курс на завтрашний день — добавляем его к сегодняшнему курсу из XML
        String jsonRate = fetchLatestJsonRate(charCode);
        if (jsonRate != null && jsonRate.equals(tomorrow.toString().substring(0, 5))) {
            return getRatesForPeriod(charCode, 2) + jsonRate;
        }

        //Возвращаем курс на сегодня
        return getRatesForPeriod(charCode, 1);
    }

    public String getRatesForPeriod(String charCodeName, int limit) {
        List<Record> recordList = fetchRecordsFromNow(charCodeName, limit + 1);
        StringBuilder stringBuilder = new StringBuilder().append(TABLE_HEADER);
        for (int i = recordList.size() - 1; i >= 1; i--) {
            double delta = recordList.get(i).getValue() - recordList.get(i - 1).getValue();
            stringBuilder.append(formatRate(LocalDate.parse(recordList.get(i).getDate(), DateTimeUtil.FORMATTER), recordList.get(i).getValue(), delta));
        }
        logger.info(stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String fetchLatestJsonRate(String charCode) {
        try {
            String jsonContent = readUrlContent(new URL(ApiUrls.CURRENCY_RATES_URL));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonContent);

            JsonNode locatedNoteValuta = rootNode.path("Valute").path(charCode);
            if (locatedNoteValuta.isMissingNode()) {
                logger.warn("Валюта {} не найдена в JSON", charCode);
                return null;
            }
            Valuta valuta = mapper.treeToValue(locatedNoteValuta, Valuta.class);
            LocalDate dateUpdated = DateTimeUtil.toLocalDate(rootNode.path("Date").asText());
            String formattedRate = formatRate(dateUpdated, valuta.getValue(), 0);
            logger.info("Успешно десериализовал JSON для {}: \n{}", charCode, formattedRate);
            logger.info(formattedRate);
            return formattedRate;
        } catch (IOException e) {
            logger.error("Ошибка при разборе JSON для {}: {}", charCode, e.getMessage(), e);
            return null;
        }
    }

    private List<Record> fetchRecordsFromNow(String charCodeName, int limit) {
        try {
            URL url = constructXmlUrl(charCodeName);
            String xmlContent = readUrlContent(url);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(xmlContent, ValCurs.class);
            logger.info("Успешно десериализовал XML для {}", charCodeName);
            if (valCurs.getRecords().isEmpty()) {
                logger.warn("Пустой список записей для {}", charCodeName);
                return Collections.emptyList();
            }
            return valCurs.getRecords().subList(Math.max(0, valCurs.getRecords().size() - limit), valCurs.getRecords().size());
        } catch (IOException e) {
            logger.error("Не удалось десериализовать XML и получить курс {} за период {}: {}", charCodeName, limit, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Получает график курса валюты за последнюю неделю по её символу (charCodeName).
     * Строит и сохраняет график в файл.
     *
     * @param charCodeName Символ валюты (например, USD, EUR).
     * @return Файл с изображением графика курса валюты или {@code null} в случае ошибки.
     */
    public File getChartRatesFromNow(String charCodeName, int limit) {
        try {
            File chart = generateChart(charCodeName, fetchRecordsFromNow(charCodeName, limit));
            logger.info("График сохранен в: {}", chart.getAbsolutePath());
            return chart;
        } catch (IOException e) {
            logger.error("Не удалось десериаллизовать XML, получить данные по {} за {} и построить график {}", charCodeName, limit, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Формирует URL для получения данных о курсе валюты за месяц.
     *
     * @param charCode Символ валюты (например, USD, EUR).
     * @return URL для получения XML данных о курсе валюты за неделю.
     */
    private URL constructXmlUrl(String charCode) throws MalformedURLException {
        logger.info("Получаю url для {}", charCode);
        String id = ValutaCharCode.valueOf(charCode).getCode();
        String dateNow = LocalDate.now().plusDays(5).format(DateTimeUtil.CB_DATE_FORMAT);
        String dateBeforeWeek = LocalDate.now().minusDays(40).format(DateTimeUtil.CB_DATE_FORMAT);

        String urlString = String.format(ApiUrls.CB_XML_URL_TEMPLATE, dateBeforeWeek, dateNow, id);
        return new URL(urlString);
    }

    /**
     * Читает содержимое URL и возвращает его как строку.
     *
     * @param url URL для чтения данных.
     * @return Строка с содержимым URL.
     * @throws IOException Если произошла ошибка при чтении данных.
     */
    private String readUrlContent(URL url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String formatRate(LocalDate date, Double rate, double delta) {
        return String.format("<code>%-6s | %11.4f    | %+6.2f</code>\n", DateTimeUtil.formatDate(date), rate, delta);
    }
}
