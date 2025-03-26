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
    public static final String CB_XML_URL_TEMPLATE =
            "https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=%s&date_req2=%s&VAL_NM_RQ=%s";

}

