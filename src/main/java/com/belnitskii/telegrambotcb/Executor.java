package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.belnitskii.telegrambotcb.TelegramBot.logger;

public abstract class Executor extends TelegramLongPollingBot {

    public void executeSafely(Object method) {
        try {
            switch (method) {
                case BotApiMethod<?> botApiMethod -> execute(botApiMethod);
                case SendPhoto sendPhoto -> execute(sendPhoto);
                case SendSticker sendSticker -> execute(sendSticker);
                case SendDocument sendDocument -> execute(sendDocument);
                case null, default -> {
                    assert method != null;
                    logger.error("Неизвестный тип команды: {}", method.getClass().getSimpleName());
                }
            }
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения команды: {}", method.getClass().getSimpleName(), e);
        }
    }
}
