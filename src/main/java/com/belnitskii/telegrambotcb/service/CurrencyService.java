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
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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
        return "Курс " + valuta.getName() + " " + valuta.getValue() + " дата обновления " + dateUpdated;
    }

    public String getWeekCurrencyRate(String charCodeName) throws IOException, java.text.ParseException {
        URL url = getUrlXmlWeek(charCodeName);
        String splitResponseUrl = splitResponseUrl(url);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StringBuilder stringBuilder = new StringBuilder();
        for (Record record : xmlMapper.readValue(splitResponseUrl, ValCurs.class).getRecords()) {
            stringBuilder.append("Дата: ").append(record.getDate()).append("\n");
            stringBuilder.append("Курс: ").append(record.getValue()).append("\n");
            stringBuilder.append("--------------------").append("\n");
        }
        return stringBuilder.toString();
    }

    private URL getUrlXmlWeek(String charCode) throws MalformedURLException {
        String id = ValutaCharCode.valueOf(charCode).getCode();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dateBeforeWeek = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        URL url = new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?" +
                "date_req1=" + dateBeforeWeek +
                "&date_req2=" + dateNow +
                "&VAL_NM_RQ=" + id);
        return url;
    }

    private String splitResponseUrl(URL url) throws IOException {
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()){
            result.append(scanner.nextLine());
        }
        return result.toString();
    }
}
