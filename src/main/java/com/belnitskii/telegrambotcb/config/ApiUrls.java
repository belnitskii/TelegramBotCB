package com.belnitskii.telegrambotcb.config;

/**
 * Класс, содержащий URL-адреса для работы с API.
 * Используется для получения курсов валют с сайта Центробанка РФ.
 */
public class ApiUrls {
    /**
     * URL для получения текущих курсов валют в формате JSON с сайта Центробанка РФ.
     */
    public static final String CURRENCY_RATES_URL = "https://www.cbr-xml-daily.ru/daily_json.js";
    public static final String CURRENCY_RATES_URL_1 = "https://www.cbr.ru/currency_base/daily/?UniDbQuery.Posted=True&UniDbQuery.To=24.03.2025";
}

