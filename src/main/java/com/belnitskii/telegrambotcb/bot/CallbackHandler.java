package com.belnitskii.telegrambotcb.bot;

import com.belnitskii.telegrambotcb.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Component
public class CallbackHandler {
    private final CurrencyService currencyService;

    @Autowired
    public CallbackHandler(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void handleCallback(Update update) throws IOException {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String currencyRate = currencyService.getCurrencyRate(callbackData);
    }

}
