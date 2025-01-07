package com.belnitskii.telegrambotcb.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler{

    public void handleCommand(Update update) {
        String command = update.getMessage().getText();

        switch (command) {
            case "/start":
                startCommandReceived(update.getMessage().getChatId(), update.getMessage().getChat().getFirstName());
                menuService.sendCurrencyMenu(update.getMessage().getChatId(), null);  // Отправка меню
                break;
            case "/help":
                // Обработка команды /help
                break;
            default:
                // Ответ на неизвестную команду
                break;
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter the currency whose official exchange rate" + "\n" +
                "you want to know in relation to BYN." + "\n" +
                "For example: USD";

    }

}
