package com.belnitskii.telegrambotcb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Класс, представляющий валюту.
 * Включает код валюты, её наименование и значение.
 * Используется для сериализации и десериализации JSON данных.
 *
 * Аннотация {@link JsonIgnoreProperties} позволяет игнорировать неизвестные свойства в JSON при десериализации.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Valuta {

    /**
     * Код валюты, например, "USD" или "EUR".
     * Элемент JSON с именем "CharCode".
     */
    @JsonProperty("CharCode")
    private String charCode;

    /**
     * Наименование валюты, например, "Доллар США" или "Евро".
     * Элемент JSON с именем "Name".
     */
    @JsonProperty("Name")
    private String name;

    /**
     * Значение валюты в числовом формате.
     * Элемент JSON с именем "Value".
     */
    @JsonProperty("Value")
    private Double value;
}
