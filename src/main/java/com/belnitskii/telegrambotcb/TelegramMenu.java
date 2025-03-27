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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.belnitskii.telegrambotcb.constant.ActionType.*;
import static com.belnitskii.telegrambotcb.constant.TelegramCommands.*;

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
                new BotCommand(START.command, START.description),
                new BotCommand(GET_RATE.command, GET_RATE.description),
                new BotCommand(ABOUT.command, ABOUT.description),
                new BotCommand(HELP.command, HELP.description)
        );
        return new SetMyCommands(commands, new BotCommandScopeDefault(), null);
    }

    public InlineKeyboardMarkup createTimeFrameMenu(String currency){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text(ACTUAL.buttonName).callbackData(currency + ACTUAL.callbackSuffix).build());
        buttons.add(row1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text(WEEK.buttonName).callbackData(currency + WEEK.callbackSuffix).build());
        row2.add(InlineKeyboardButton.builder().text(TWO_WEEKS.buttonName).callbackData(currency + TWO_WEEKS.callbackSuffix).build());
        row2.add(InlineKeyboardButton.builder().text(MONTH.buttonName).callbackData(currency + MONTH.callbackSuffix).build());
        buttons.add(row2);
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup createSecondTimeFrameMenu(String currency){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text(TEXT.buttonName).callbackData(currency + TEXT.callbackSuffix).build());
        row.add(InlineKeyboardButton.builder().text(CHART.buttonName).callbackData(currency + CHART.callbackSuffix).build());
        buttons.add(row);
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup createCurrencyMenu(){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text(String.valueOf(ValutaCharCode.USD))
                .callbackData(String.valueOf(ValutaCharCode.USD))
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text(String.valueOf(ValutaCharCode.EUR))
                .callbackData(String.valueOf(ValutaCharCode.EUR))
                .build());
        buttons.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text(OTHER_CURRENCY.buttonName)
                .callbackData(OTHER_CURRENCY.callbackSuffix)
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
        row1.add(new KeyboardButton(GET_RATE.buttonName));
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(HELP.buttonName));
        row2.add(new KeyboardButton(ABOUT.buttonName));
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
