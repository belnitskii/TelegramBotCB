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

@Service
public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    public String getCurrencyRate(String charCode) throws ParseException, IOException {
        String splitResponseUrl = splitResponseUrl(new URL(ApiUrls.CURRENCY_RATES_URL));
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(splitResponseUrl);
            JsonNode locatedNoteValuta = rootNode.path("Valute").path(charCode);
            Valuta valuta = mapper.readValue(locatedNoteValuta.toString(), Valuta.class);
            JsonNode dateTimeNode = rootNode.path("Date");
            LocalDate dateUpdated = DateTimeUtil.toLocalDate(dateTimeNode.toString());
            logger.info("Успешно десериализовал JSON для {}", charCode);
            String rate = MessageFormat.format("Курс {0} к RUB {1} \n1 {2} = {3} RUB", valuta.getCharCode(), dateUpdated, valuta.getCharCode(), valuta.getValue() );
            logger.info(rate);
            return rate;
        } catch (RuntimeException e) {
            logger.error("Не удалось десериаллизовать JSON и получить курс для {}", charCode);
            return null;
        }
    }

    public String getWeekCurrencyRate(String charCodeName) throws IOException, java.text.ParseException {
        URL url = getUrlXmlWeek(charCodeName);
        String splitResponseUrl = splitResponseUrl(url);
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
            logger.info("Успешно десериализовал XML для {}", charCodeName);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Курс ").append(charCodeName).append(" к RUB за ").append(valCurs.getRecords().size()).append(" дней.\n(");
            stringBuilder.append(valCurs.getRecords().getLast().getDate()).append(" — ").append(valCurs.getRecords().getFirst().getDate()).append(")\n\n");
            for (int i = valCurs.getRecords().size() - 1; i >= 0; i--) {
                stringBuilder.append("1 ").append(charCodeName).append(" = ").append(String.format("%.2f", valCurs.getRecords().get(i).getValue())).append(" RUB\n");
            }
            logger.info(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (RuntimeException e) {
            logger.error("Не удалось десериаллизовать XML и получить курс {} за неделю", charCodeName);
            return null;
        }
    }

    public File getWeekChartCurrencyRate(String charCodeName) throws IOException {
        URL url = getUrlXmlWeek(charCodeName);
        String splitResponseUrl = splitResponseUrl(url);
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
            logger.info("Успешно десериализовал XML для {}", charCodeName);
            File chart = generateChart(charCodeName, valCurs.getRecords());
            logger.info("График сохранен в: {}", chart.getAbsolutePath());
            return chart;
        } catch (RuntimeException e) {
            logger.error("Не удалось десериаллизовать XML, получить данные по {} за неделю и построить график", charCodeName);
            return null;
        }
    }

    private URL getUrlXmlWeek(String charCode) throws MalformedURLException {
        logger.info("Получаю url для {}", charCode);
        String id = ValutaCharCode.valueOf(charCode).getCode();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dateBeforeWeek = LocalDate.now().minusDays(8).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        URL urlXml = new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?" +
                "date_req1=" + dateBeforeWeek +
                "&date_req2=" + dateNow +
                "&VAL_NM_RQ=" + id);
        logger.info("URL {} – {}", charCode, urlXml);
        return urlXml;
    }

    private String splitResponseUrl(URL url) throws IOException {
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }
        return result.toString();
    }
}
