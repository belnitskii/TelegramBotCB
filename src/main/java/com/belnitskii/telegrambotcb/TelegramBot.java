package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import com.belnitskii.telegrambotcb.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CurrencyService currencyService;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            if (callbackData.equals("USD") || callbackData.equals("EUR")) {
                sendTimeFrameMenu(chatId, messageId, callbackData);
            } else if (callbackData.startsWith("RATE_")) {
                // Если выбрали период, показываем курс (заглушка)
                String[] parts = callbackData.split("_");
                String currency = parts[1];
                String period = parts[2];
                String rate = "null";
                if (callbackData.endsWith("_TODAY")){
                    try {
                        rate = currencyService.getCurrencyRate(currency); // 👈 Вызываем CurrencyService
                    } catch (IOException e) {
                        rate = "Ошибка при получении курса валюты.";
                    }
                }

                if (callbackData.endsWith("_WEEK")){
                    try {
                        rate = currencyService.getWeekCurrencyRate(currency); // 👈 Вызываем CurrencyService
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                editMessageWithRate(chatId, messageId,  rate);
            }
        }
    }


    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter the currency whose official exchange rate" + "\n" +
                "you want to know in relation to BYN." + "\n" +
                "For example: USD";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(answer);
        message.setReplyMarkup(createCurrencyMenu());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTimeFrameMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText("Выберите период для " + currency + ":");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Сегодня").callbackData("RATE_" + currency + "_TODAY").build());
        row.add(InlineKeyboardButton.builder().text("За неделю").callbackData("RATE_" + currency + "_WEEK").build());
        buttons.add(row);

        keyboardMarkup.setKeyboard(buttons);
        editMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createCurrencyMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("USD")
                .callbackData("USD")
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text("EUR")
                .callbackData("EUR")
                .build());
        buttons.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("GBP")
                .callbackData("GBP")
                .build());
        buttons.add(row2);

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    private void editMessageWithRate(Long chatId, Integer messageId, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

