package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.constant.ValutaCharCode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TelegramMenu {

    public InlineKeyboardMarkup createCharCodeMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = Arrays.stream(ValutaCharCode.values())
                .map(charCode -> List.of(InlineKeyboardButton.builder()
                        .text(charCode.name() + " — " + charCode.getName())
                        .callbackData(charCode.name())
                        .build()))
                .collect(Collectors.toList());
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    public SetMyCommands createBotCommands() {
        List<BotCommand> commands = List.of(
                new BotCommand("/get", "Get rate"),
                new BotCommand("/start", "Start bot"),
                new BotCommand("/help", "Help"),
                new BotCommand("/about", "About program")
        );
        SetMyCommands setMyCommands = new SetMyCommands(commands, new BotCommandScopeDefault(), null);
        return setMyCommands;
    }

    public InlineKeyboardMarkup createTimeFrameMenu(String currency){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Actual").callbackData(currency + "_ACTUAL").build());
        row.add(InlineKeyboardButton.builder().text("Week").callbackData(currency + "_WEEK").build());
        buttons.add(row);
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup createSecondTimeFrameMenu(String currency){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Text").callbackData(currency + "_TEXT").build());
        row.add(InlineKeyboardButton.builder().text("Chart").callbackData(currency + "_CHART").build());
        buttons.add(row);
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup createCurrencyMenu(){
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
                .text("Other Currency")
                .callbackData("Other Currency")
                .build());
        buttons.add(row2);

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup createReplyKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Get rate"));
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Help"));
        row2.add(new KeyboardButton("About program"));
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
