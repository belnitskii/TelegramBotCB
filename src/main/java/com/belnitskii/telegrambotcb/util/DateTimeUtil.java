package com.belnitskii.telegrambotcb.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    public static LocalDate toLocalDate(String dateTimeStr) {
        try {
            String cleanedDateTimeStr = dateTimeStr.replace("\"", "");
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(cleanedDateTimeStr);
            return offsetDateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка преобразования даты: " + e.getMessage());
            return null;
        }
    }
}
