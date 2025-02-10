package com.belnitskii.telegrambotcb.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

/**
 * Класс, представляющий курс валют.
 * Включает идентификатор, диапазоны дат, название валюты и список записей с данными о валютных курсах.
 * Используется для сериализации и десериализации XML данных.
 */
@Data
public class ValCurs {

    /**
     * Внутренний код Центробанка, например "R01235".
     * Атрибут XML с именем "ID"
     */
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    private String id;

    /**
     * Начальная дата, выбранного диапазона, в который попадает курс.
     * Например DateRange1="10.01.2024" DateRange2="20.01.2024"
     * Атрибут XML с именем "DateRange1".
     */
    @JacksonXmlProperty(localName = "DateRange1", isAttribute = true)
    private String dateRange1;

    /**
     * Конечная дата, выбранного диапазона, в который попадает курс.
     * Например DateRange1="10.01.2024" DateRange2="20.01.2024"
     * Атрибут XML с именем "DateRange2".
     */
    @JacksonXmlProperty(localName = "DateRange2", isAttribute = true)
    private String dateRange2;

    /**
     * Список записей о валютных курсах.
     * Каждая запись содержит данные о дате, номинале и значении валюты.
     * Элемент XML с именем "Record".
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Record")
    private List<Record> records;
}
