package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import com.belnitskii.telegrambotcb.model.Valuta;
import com.belnitskii.telegrambotcb.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

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
        Valuta valuta = new Valuta();
        String currency = "";

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    sendCurrencyMenu(chatId, null);
                    break;
                default:
                    try {
                        currency = CurrencyService.getCurrencyRate(messageText);

                    } catch (IOException e) {
                        sendMessage(chatId, "We have not found such a currency." + "\n" +
                                "Enter the currency whose official exchange rate" + "\n" +
                                "you want to know in relation to BYN." + "\n" +
                                "For example: USD");
                    } catch (ParseException e) {
                        throw new RuntimeException("Unable to parse date");
                    }
                    sendMessage(chatId, currency);
            }
        } else if (update.hasCallbackQuery()) {
            String currencyCode = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            // Удаляем старое сообщение с кнопками
            deleteMessage(chatId, messageId);

            // Отправляем ответ
            String rate = null; // Метод для получения курса
            try {
                rate = CurrencyService.getCurrencyRate(currencyCode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sendMessage(chatId, rate);
            // Отображаем новое меню после ответа
            sendCurrencyMenu(chatId, null);
        }

    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter the currency whose official exchange rate" + "\n" +
                "you want to know in relation to BYN." + "\n" +
                "For example: USD";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
        }
    }

    private void sendCurrencyMenu(Long chatId, Integer oldMessageId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите валюту:");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Создание кнопок с использованием билдера
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
        message.setReplyMarkup(keyboardMarkup);

        try {
            // Удаляем старое сообщение с кнопками, если передан oldMessageId
            if (oldMessageId != null) {
                deleteMessage(chatId, oldMessageId);
            }
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
