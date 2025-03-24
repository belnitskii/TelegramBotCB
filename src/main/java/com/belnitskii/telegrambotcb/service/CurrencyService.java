package com.belnitskii.telegrambotcb.service;

import com.belnitskii.telegrambotcb.config.ApiUrls;
import com.belnitskii.telegrambotcb.constant.ValutaCharCode;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static com.belnitskii.telegrambotcb.util.ChartUtil.generateChart;

/**
 * Сервис для работы с валютными курсами.
 * Содержит методы для получения курса валюты, курса валюты за неделю и построения графика курса валюты.
 * Работает с API Центробанка России.
 */
@Service
public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    /**
     * Получает текущий курс валюты по её символу (charCode) и возвращает строку с курсом.
     *
     * @param charCode Символ валюты (например, USD, EUR).
     * @return Строка с курсом валюты или {@code null} в случае ошибки.
     * @throws ParseException Если возникла ошибка при парсинге данных.
     */
    public String getCurrencyRate(String charCode) {
        try {
            String splitResponseUrl = splitResponseUrl(new URL(ApiUrls.CURRENCY_RATES_URL));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(splitResponseUrl);
            JsonNode locatedNoteValuta = rootNode.path("Valute").path(charCode);
            Valuta valuta = mapper.readValue(locatedNoteValuta.toString(), Valuta.class);
            JsonNode dateTimeNode = rootNode.path("Date");
            LocalDate dateUpdated = DateTimeUtil.toLocalDate(dateTimeNode.toString());
            StringBuilder stringBuilder = new StringBuilder();
            String rate = MessageFormat.format("<code>1 {0} = {1} RUB | {2}.{3}</code>",
                    valuta.getCharCode(),
                    valuta.getValue().toString(),
                    dateUpdated.getDayOfMonth(),
                    String.format("%02d", dateUpdated.getMonthValue()));
            logger.info("Успешно десериализовал JSON для {}", charCode);
            logger.info(rate);
            String rate2 = getCurrencyRateXML(charCode);
            stringBuilder.append(rate).append("\n").append(rate2);
            return stringBuilder.toString();
        } catch (IOException e) {
            logger.error("Не удалось десериаллизовать JSON и получить курс для {}", charCode);
            return null;
        }
    }

    public String getCurrencyRateXML(String charCodeName) {
        try {
            URL url = getUrlXmlDay(charCodeName);
            String splitResponseUrl = splitResponseUrl(url);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
            logger.info("Успешно десериализовал XML для {}", charCodeName);
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder
                    .append("<code>1 ")
                    .append(charCodeName)
                    .append(" = ")
                    .append(String.format("%.4f", valCurs.getRecords().getLast().getValue()))
                    .append(" RUB | ")
                    .append(valCurs.getRecords().getLast().getDate().substring(0, 5))
                    .append("</code>\n");

            logger.info(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException | ParseException e) {
            logger.error("Не удалось десериаллизовать XML и получить курс {} за неделю", charCodeName);
            return null;
        }
    }

    /**
     * Получает курс валюты за последнюю неделю по её символу (charCodeName) и возвращает строку с курсами.
     * Форматирует строку для вывода всех курсов за неделю.
     *
     * @param charCodeName Символ валюты (например, USD, EUR).
     * @return Строка с курсами валюты за неделю или {@code null} в случае ошибки.
     * @throws ParseException Если возникла ошибка при парсинге данных.
     */
    public String getWeekCurrencyRate(String charCodeName) {
        try {
            URL url = getUrlXmlWeek(charCodeName);
            String splitResponseUrl = splitResponseUrl(url);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
            logger.info("Успешно десериализовал XML для {}", charCodeName);
            StringBuilder stringBuilder = new StringBuilder();
            int limit = 7;
            for (int i = valCurs.getRecords().size() - 1; i >= 0 && limit > 0; i--, limit--) {
                stringBuilder
                        .append("<code>1 ")
                        .append(charCodeName)
                        .append(" = ")
                        .append(String.format("%.4f", valCurs.getRecords().get(i).getValue()))
                        .append(" RUB | ")
                        .append(valCurs.getRecords().get(i).getDate().substring(0, 5))
                        .append("</code>\n");
            }
            logger.info(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException | ParseException e) {
            logger.error("Не удалось десериаллизовать XML и получить курс {} за неделю", charCodeName);
            return null;
        }
    }

    /**
     * Получает график курса валюты за последнюю неделю по её символу (charCodeName).
     * Строит и сохраняет график в файл.
     *
     * @param charCodeName Символ валюты (например, USD, EUR).
     * @return Файл с изображением графика курса валюты или {@code null} в случае ошибки.
     */
    public File getWeekChartCurrencyRate(String charCodeName) {
        try {
            URL url = getUrlXmlWeek(charCodeName);
            String splitResponseUrl = splitResponseUrl(url);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
            logger.info("Успешно десериализовал XML для {}", charCodeName);
            File chart = generateChart(charCodeName, valCurs.getRecords());
            logger.info("График сохранен в: {}", chart.getAbsolutePath());
            return chart;
        } catch (IOException e) {
            logger.error("Не удалось десериаллизовать XML, получить данные по {} за неделю и построить график", charCodeName);
            return null;
        }
    }

    /**
     * Формирует URL для получения данных о курсе валюты за неделю.
     *
     * @param charCode Символ валюты (например, USD, EUR).
     * @return URL для получения XML данных о курсе валюты за неделю.
     */
    private URL getUrlXmlWeek(String charCode) {
        try {
            logger.info("Получаю url для {}", charCode);
            String id = ValutaCharCode.valueOf(charCode).getCode();
            String dateNow = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String dateBeforeWeek = LocalDate.now().minusDays(11).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            URL urlXml = new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?" +
                    "date_req1=" + dateBeforeWeek +
                    "&date_req2=" + dateNow +
                    "&VAL_NM_RQ=" + id);
            logger.info("URL {} – {}", charCode, urlXml);
            return urlXml;
        } catch (MalformedURLException e){
            logger.error("Не удалось получить url для XML ", e);
            return null;
        }
    }

    private URL getUrlXmlDay(String charCode) {
        try {
            logger.info("Получаю url для {}", charCode);
            String id = ValutaCharCode.valueOf(charCode).getCode();
            String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String dateBeforeWeek = LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            URL urlXml = new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?" +
                    "date_req1=" + dateBeforeWeek +
                    "&date_req2=" + dateNow +
                    "&VAL_NM_RQ=" + id);
            logger.info("URL {} – {}", charCode, urlXml);
            return urlXml;
        } catch (MalformedURLException e){
            logger.error("Не удалось получить url для XML ", e);
            return null;
        }
    }

    /**
     * Читает содержимое URL и возвращает его как строку.
     *
     * @param url URL для чтения данных.
     * @return Строка с содержимым URL.
     * @throws IOException Если произошла ошибка при чтении данных.
     */
    private String splitResponseUrl(URL url) throws IOException {
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }
        return result.toString();
    }
}
