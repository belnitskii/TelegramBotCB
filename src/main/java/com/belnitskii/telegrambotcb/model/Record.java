package com.belnitskii.telegrambotcb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * Класс, представляющий запись о валютном курсе, полученную с сайта Центробанка РФ.
 * Включает дату, номинал и значение валюты.
 * Используется для сериализации и десериализации XML данных.
 */
@Data
public class Record {

    /**
     * Дата, на которую зафиксирован курс валюты.
     * Атрибут XML с именем "Date".
     */
    @JacksonXmlProperty(localName = "Date", isAttribute = true)
    private String date;

    /**
     * Значение валюты в формате String, так как данные приходят в формате double, но с запятой вместо точки, например "87,6457".
     * Элемент XML с именем "Value".
     */
    @JacksonXmlProperty(localName = "Value")
    private String rawValue;

    /**
     * Получает значение валюты в виде double.
     * Преобразует строку из поля {@code rawValue} в число, заменяя запятую на точку.
     *
     * @return значение валюты как {@code double}.
     * @throws NumberFormatException если строка не может быть преобразована в число.
     */
    @JsonIgnore
    public double getValue() {
        return Double.parseDouble(rawValue.replace(",", "."));
    }
}
