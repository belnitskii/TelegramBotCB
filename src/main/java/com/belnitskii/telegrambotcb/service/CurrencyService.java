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
import java.util.List;
import java.util.Scanner;

@Service
public class CurrencyService {

    public String getCurrencyRate(String message) throws ParseException, IOException {
        URL url = new URL(ApiUrls.CURRENCY_RATES_URL);
        String result = splitResponse(url);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode locatedNoteValuta = rootNode.path("Valute").path(message);
        Valuta valuta = mapper.readValue(locatedNoteValuta.toString(), Valuta.class);
        JsonNode dateTimeNode = rootNode.path("Date");
        LocalDate dateUpdated = DateTimeUtil.toLocalDate(dateTimeNode.toString());
        return "Курс " + valuta.getName() + " " +  valuta.getValue() + " дата обновления " + dateUpdated;
    }



    public String getWeekCurrencyRate(String message) throws IOException, java.text.ParseException {
        URL url = getUrlXmlWeek(message);
        String result = splitResponse(url);
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ValCurs valCurs = xmlMapper.readValue(result, ValCurs.class);
            List<Record> records = valCurs.getRecords();
            StringBuilder stringBuilder = new StringBuilder();
            for (Record record : records) {
                stringBuilder.append("Дата: ").append(record.getDate()).append("\n");
                stringBuilder.append("Курс: ").append(record.getValue()).append("\n");
                stringBuilder.append("--------------------").append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private URL getUrlXmlWeek(String message) throws MalformedURLException {
        String code = ValutaCharCode.valueOf(message).code;
        LocalDate date = LocalDate.now();
        String formattedDateNow = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println(formattedDateNow);

        LocalDate dateBefore = LocalDate.now().minusDays(7);
        String formattedDateBefore = dateBefore.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println(formattedDateBefore);
        URL url = new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" +
                formattedDateBefore +
                "&date_req2=" +
                formattedDateNow +
                "&VAL_NM_RQ=" +
                code);
        return url;

    }
    private String splitResponse(URL url) throws IOException {
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        return result;
    }
}
