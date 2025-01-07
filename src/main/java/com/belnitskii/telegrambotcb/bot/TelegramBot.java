package com.belnitskii.telegrambotcb.bot;

import com.belnitskii.telegrambotcb.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Component
public class TelegramBot extends TelegramLongPollingBot{

    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;
    private final BotConfig botConfig;
    private final MenuService menuService;


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Autowired
    public TelegramBot(CommandHandler commandHandler, CallbackHandler callbackHandler, BotConfig botConfig, MenuService menuService) {
        this.commandHandler = commandHandler;
        this.callbackHandler = callbackHandler;
        this.botConfig = botConfig;
        this.menuService = menuService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            commandHandler.handleCommand(update);  // Обработка команды
        } else if (update.hasCallbackQuery()) {
            try {
                callbackHandler.handleCallback(update);  // Обработка callback
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            execute(sendMessage);  // Используем метод execute() бота для отправки сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();  // Можно добавить обработку ошибки
        }
    }


}

