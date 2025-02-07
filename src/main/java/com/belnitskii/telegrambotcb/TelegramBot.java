package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import com.belnitskii.telegrambotcb.constant.ValutaCharCode;
import com.belnitskii.telegrambotcb.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

            if (ValutaCharCode.Contain(callbackData)) {
                sendTimeFrameMenu(chatId, messageId, callbackData);
            }
            if (callbackData.endsWith("_WEEK")) {
                sendSecondTimeFrameMenu(chatId, messageId, callbackData);
            }
            if (callbackData.endsWith("_TEXT")) {
                String currency = callbackData.split("_")[0];
                String rate;
                try {
                    rate = currencyService.getWeekCurrencyRate(currency);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                editMessageWithRate(chatId, messageId, rate);
            }
            if (callbackData.endsWith("_CHART")) {
                String currency = callbackData.split("_")[0];
                try {
                    File chart = currencyService.getWeekChartCurrencyRate(currency);
                    sendChart(String.valueOf(chatId), chart);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                editMessageWithRate(chatId, messageId, "Вот ваш график");
            }
            if (callbackData.endsWith("_TODAY")) {
                String currency = callbackData.split("_")[0];
                String rate;
                try {
                    rate = currencyService.getCurrencyRate(currency);
                } catch (IOException e) {
                    rate = "Ошибка при получении курса валюты.";
                }
                editMessageWithRate(chatId, messageId, rate);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здравствуйте, " + name + ", выберите валюту";
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
        row.add(InlineKeyboardButton.builder().text("Сегодня").callbackData(currency + "_TODAY").build());
        row.add(InlineKeyboardButton.builder().text("За неделю").callbackData(currency + "_WEEK").build());
        buttons.add(row);

        keyboardMarkup.setKeyboard(buttons);
        editMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSecondTimeFrameMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText("Выберите удобный вариант отображения " + currency + ":");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Текст").callbackData(currency + "_TEXT").build());
        row.add(InlineKeyboardButton.builder().text("График").callbackData(currency + "_CHART").build());
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

    private void sendChart(String chatId, File chart) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(chart));

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

