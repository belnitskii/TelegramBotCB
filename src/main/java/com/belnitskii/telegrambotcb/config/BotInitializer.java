package com.belnitskii.telegrambotcb.config;

import com.belnitskii.telegrambotcb.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Инициализатор Telegram-бота.
 * <p>
 * Этот класс отвечает за регистрацию бота в API Telegram при старте Spring-приложения.
 * Он слушает событие {@link ContextRefreshedEvent} и запускает процесс инициализации бота.
 * </p>
 */
@Component
public class BotInitializer {
    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    /**
     * Экземпляр Telegram-бота, который будет зарегистрирован.
     */
    private final TelegramBot telegramBot;

    /**
     * Конструктор, который получает зависимость {@link TelegramBot} через инъекцию Spring.
     *
     * @param telegramBot Экземпляр бота, который нужно зарегистрировать.
     */
    @Autowired
    public BotInitializer(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Метод инициализирует и регистрирует бота в API Telegram.
     * <p>
     * Этот метод автоматически вызывается при запуске приложения, так как слушает событие {@link ContextRefreshedEvent}.
     * </p>
     *
     * @throws TelegramApiException если возникла ошибка при регистрации бота.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init()throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e){
            logger.error("Не удалось зарегистрировать бота", e);
        }
    }
}
