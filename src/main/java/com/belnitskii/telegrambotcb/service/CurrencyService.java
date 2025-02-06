package com.belnitskii.telegrambotcb.service;

import com.belnitskii.telegrambotcb.config.ApiUrls;
import com.belnitskii.telegrambotcb.model.Valuta;
import com.belnitskii.telegrambotcb.util.DateTimeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

@Service
public class CurrencyService {

    public String getCurrencyRate(String message) throws IOException, ParseException, IOException {
        URL url = new URL(ApiUrls.CURRENCY_RATES_URL);
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()){
            result +=scanner.nextLine();
        }
        System.out.println(result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode locatedNoteValuta = rootNode.path("Valute").path(message);
        System.out.println(locatedNoteValuta);
        Valuta valuta = mapper.readValue(locatedNoteValuta.toString(), Valuta.class);
        System.out.println(valuta.toString());
        JsonNode dateTimeNode = rootNode.path("Date");
        LocalDate dateUpdated = DateTimeUtil.toLocalDate(dateTimeNode.toString());
        return "Курс " + valuta.getName() + " " +  valuta.getValue() + " дата обновления " + dateUpdated;
    }

    public String getWeekCurrencyRate(String message) throws IOException {
        URL url = new URL("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=10/01/2022&date_req2=20/01/2022&VAL_NM_RQ=R01235");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()){
            result +=scanner.nextLine();
        }

        return "test-test-test";
    }
}
