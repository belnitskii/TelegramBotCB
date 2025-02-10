package com.belnitskii.telegrambotcb.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Конфигурационный класс для Telegram-бота.
 * <p>
 * Загружает свойства бота из файла конфигурации <code>application.properties</code>.
 * Использует аннотации Spring для автоматической привязки значений.
 * </p>
 */
@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    /**
     * Имя бота, загружаемое из <code>application.properties</code>.
     */
    @Value("${bot.name}")
    String botName;

    /**
     * Токен бота, используемый для аутентификации в Telegram API.
     * Загружается из <code>application.properties</code>.
     */
    @Value("${bot.token}")
    String token;
}
