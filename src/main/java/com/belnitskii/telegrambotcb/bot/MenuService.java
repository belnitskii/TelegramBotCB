package com.belnitskii.telegrambotcb.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {
    public void sendCurrencyMenu(Long chatId, Integer oldMessageId) {
        InlineKeyboardMarkup menu = createCurrencyMenu();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите валюту:");
        message.setReplyMarkup(menu);

    }

    private InlineKeyboardMarkup createCurrencyMenu() {
        SendMessage message = new SendMessage();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        // Создание кнопок
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
        return null;
//
//        try {
//            // Удаляем старое сообщение с кнопками, если передан oldMessageId
//            if (oldMessageId != null) {
//                deleteMessage(chatId, oldMessageId);
//            }
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

}
