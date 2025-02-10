package com.belnitskii.telegrambotcb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Утилитный класс для работы с датами и временем.
 * Предоставляет метод для преобразования строки в {@link LocalDate}.
 */
public class DateTimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    /**
     * Преобразует строковое представление даты и времени в объект {@link LocalDate}.
     *
     * <p>Метод очищает строку от символов кавычек и пытается разобрать строку
     * как {@link OffsetDateTime}, после чего извлекает только дату в формате {@link LocalDate}.
     * Если строка не может быть корректно преобразована, метод логирует ошибку
     * и возвращает {@code null}.</p>
     *
     * @param dateTimeStr строка, представляющая дату и время в формате ISO 8601,
     *                    например: "2025-02-10T15:30:00+03:00"
     * @return объект {@link LocalDate}, содержащий только дату из строки,
     * или {@code null} в случае ошибки преобразования
     */
    public static LocalDate toLocalDate(String dateTimeStr) {
        try {
            String cleanedDateTimeStr = dateTimeStr.replace("\"", "");
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(cleanedDateTimeStr);
            return offsetDateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            logger.error("Ошибка преобразования даты: ", e);
            return null;
        }
    }
}
