package com.belnitskii.telegrambotcb.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceTest {
    private final CurrencyService currencyService = new CurrencyService();

    @Test
    void getLatestRate_byNull() {
        assertNull(currencyService.getLatestRate(null));
    }

    @Test
    void getLatestRate_byUnexpectedCharCode() {
        assertNull(currencyService.getLatestRate("$#!%"));
    }

    @Test
    void getLatestRate_Success() {
        assertFalse(currencyService.getLatestRate("USD").isEmpty());
    }

    @Test
    void getLatestRate_ByDiapozon_byNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> currencyService.getChartRatesFromNow(null, 7));
        assertEquals("Name is null", exception.getMessage());
    }

    @Test
    void getLatestRate_ByDiapozon_byUnexpectedCharCode() {
        String unexpectedCharCode = "$#!%";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> assertNull(currencyService.getRatesFromNow(unexpectedCharCode, 7)));
        assertEquals("No enum constant com.belnitskii.telegrambotcb.constant.ValutaCharCode." + unexpectedCharCode, exception.getMessage());
    }

    @Test
    void getLatestRate_ByDiapozon_Success() {
        assertFalse(currencyService.getRatesFromNow("USD", 7).isEmpty());
    }

    @Test
    void getChartCurrencyRate_ByPeriodFromNow_byNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> currencyService.getChartRatesFromNow(null, 7));
        assertEquals("Name is null", exception.getMessage());
    }

    @Test
    void getChartCurrencyRate_ByPeriodFromNow_byUnexpectedCharCode() {
        String unexpectedCharCode = "$#!%";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> assertNull(currencyService.getChartRatesFromNow(unexpectedCharCode, 7)));
        assertEquals("No enum constant com.belnitskii.telegrambotcb.constant.ValutaCharCode." + unexpectedCharCode, exception.getMessage());
    }

    @Test
    void getChartCurrencyRate_ByPeriodFromNow_Success() {
        assertTrue(currencyService.getChartRatesFromNow("USD", 7).isFile());
    }
}