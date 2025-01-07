package com.belnitskii.telegrambotcb.service;

import com.belnitskii.telegrambotcb.model.Valuta;
import com.belnitskii.telegrambotcb.util.DateTimeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.expression.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

public class CurrencyService {

    public static String getCurrencyRate(String message) throws IOException, ParseException, IOException {
        URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
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
        JsonNode dateTimeNode = rootNode.path("Date");
        LocalDate dateUpdated = DateTimeUtil.toLocalDate(dateTimeNode.toString());
        return "Курс " + valuta.getName() + " " +  valuta.getValue() + " дата обновления " + dateUpdated;
    }
}
