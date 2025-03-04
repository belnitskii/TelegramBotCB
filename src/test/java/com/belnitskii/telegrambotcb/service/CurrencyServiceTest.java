package com.belnitskii.telegrambotcb.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceTest {
    private final CurrencyService currencyService = new CurrencyService();

    @Test
    void getCurrencyRate_byNull() {
        assertNull(currencyService.getCurrencyRate(null));
    }

    @Test
    void getCurrencyRate_byUnexpectedCharCode() {
        assertNull(currencyService.getCurrencyRate("$#!%"));
    }

    @Test
    void getCurrencyRate_Success() {
        assertFalse(currencyService.getCurrencyRate("USD").isEmpty());
    }

    @Test
    void getWeekCurrencyRate_byNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> currencyService.getWeekChartCurrencyRate(null));
        assertEquals("Name is null", exception.getMessage());
    }

    @Test
    void getWeekCurrencyRate_byUnexpectedCharCode() {
        String unexpectedCharCode = "$#!%";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> assertNull(currencyService.getWeekCurrencyRate(unexpectedCharCode)));
        assertEquals("No enum constant com.belnitskii.telegrambotcb.constant.ValutaCharCode." + unexpectedCharCode, exception.getMessage());
    }

    @Test
    void getWeekCurrencyRate_Success() {
        assertFalse(currencyService.getWeekCurrencyRate("USD").isEmpty());
    }

    @Test
    void getWeekChartCurrencyRate_byNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> currencyService.getWeekChartCurrencyRate(null));
        assertEquals("Name is null", exception.getMessage());
    }

    @Test
    void getWeekChartCurrencyRate_byUnexpectedCharCode() {
        String unexpectedCharCode = "$#!%";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> assertNull(currencyService.getWeekChartCurrencyRate(unexpectedCharCode)));
        assertEquals("No enum constant com.belnitskii.telegrambotcb.constant.ValutaCharCode." + unexpectedCharCode, exception.getMessage());
    }

    @Test
    void getWeekChartCurrencyRate_Success() {
        assertTrue(currencyService.getWeekChartCurrencyRate("USD").isFile());
    }
}