package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramBotTest {

    @InjectMocks
    TelegramBot telegramBot;

    @Mock
    BotConfig botConfig;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(botConfig.getBotName()).thenReturn("TestBotName");
        Mockito.lenient().when(botConfig.getToken()).thenReturn("TestBotToken");
    }

    @Test
    void getBotUsername() {
        Assertions.assertEquals(telegramBot.getBotUsername(), botConfig.getBotName());
    }

    @Test
    void getBotToken() {
        Assertions.assertEquals(telegramBot.getBotToken(), botConfig.getToken());
    }
}