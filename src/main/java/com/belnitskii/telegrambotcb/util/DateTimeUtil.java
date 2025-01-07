package com.belnitskii.telegrambotcb.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    public static LocalDate toLocalDate(String dateTimeStr) {
        try {
            // Удаляем кавычки из строки, если они присутствуют
            String cleanedDateTimeStr = dateTimeStr.replace("\"", "");

            // Разбор строки как OffsetDateTime
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(cleanedDateTimeStr);

            // Преобразование в LocalDate
            return offsetDateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            // Логирование ошибки или обработка исключения
            System.err.println("Ошибка преобразования даты: " + e.getMessage());
            return null;
        }
    }

}
