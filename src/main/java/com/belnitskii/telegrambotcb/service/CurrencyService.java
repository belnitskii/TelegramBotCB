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
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static com.belnitskii.telegrambotcb.util.ChartUtil.generateChart;

@Service
public class CurrencyService {
    public String getCurrencyRate(String charCode) throws ParseException, IOException {
        String splitResponseUrl = splitResponseUrl(new URL(ApiUrls.CURRENCY_RATES_URL));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(splitResponseUrl);
        JsonNode locatedNoteValuta = rootNode.path("Valute").path(charCode);
        Valuta valuta = mapper.readValue(locatedNoteValuta.toString(), Valuta.class);
        JsonNode dateTimeNode = rootNode.path("Date");
        LocalDate dateUpdated = DateTimeUtil.toLocalDate(dateTimeNode.toString());
        return "Курс " + valuta.getCharCode() + " к RUB " + dateUpdated + "\n1 " + valuta.getCharCode() + " = " + valuta.getValue() + " RUB";
    }

    public String getWeekCurrencyRate(String charCodeName) throws IOException, java.text.ParseException {
        URL url = getUrlXmlWeek(charCodeName);
        String splitResponseUrl = splitResponseUrl(url);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Курс ").append(charCodeName).append(" к RUB за ").append(valCurs.getRecords().size()).append(" дней.\n(");
        stringBuilder.append(valCurs.getRecords().getLast().getDate()).append(" — ").append(valCurs.getRecords().getFirst().getDate()).append(")\n\n");
        for (int i = valCurs.getRecords().size() - 1; i >= 0; i--) {
            stringBuilder.append("1 ").append(charCodeName).append(" = ").append(String.format("%.2f", valCurs.getRecords().get(i).getValue())).append(" RUB\n");
        }
        return stringBuilder.toString();
    }

    public File getWeekChartCurrencyRate(String charCodeName) throws IOException {
        URL url = getUrlXmlWeek(charCodeName);
        String splitResponseUrl = splitResponseUrl(url);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ValCurs valCurs = xmlMapper.readValue(splitResponseUrl, ValCurs.class);
        File chart = generateChart(charCodeName, valCurs.getRecords());
        System.out.println("График сохранен в: " + chart.getAbsolutePath());
        return chart;
    }

    private URL getUrlXmlWeek(String charCode) throws MalformedURLException {
        String id = ValutaCharCode.valueOf(charCode).getCode();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dateBeforeWeek = LocalDate.now().minusDays(8).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?" +
                "date_req1=" + dateBeforeWeek +
                "&date_req2=" + dateNow +
                "&VAL_NM_RQ=" + id);
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
