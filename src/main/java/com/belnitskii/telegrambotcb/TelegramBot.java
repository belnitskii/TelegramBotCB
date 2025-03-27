package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import com.belnitskii.telegrambotcb.constant.ValutaCharCode;
import com.belnitskii.telegrambotcb.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.belnitskii.telegrambotcb.constant.TelegramCommands.*;

/**
 * Основной класс Telegram-бота, обрабатывающий входящие сообщения и команды.
 * <p>
 * Этот класс отвечает за взаимодействие с Telegram API, обработку пользовательских команд
 * и отправку ответов пользователям.
 * </p>
 */
@Component
@AllArgsConstructor
public class TelegramBot extends Executor {
    static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig botConfig;
    private final CurrencyService currencyService;
    private final TelegramMenu telegramMenu;

    /**
     * Получает имя бота.
     *
     * @return Имя бота, указанное в конфигурации.
     */
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    /**
     * Получает токен бота.
     *
     * @return Токен для авторизации в Telegram API.
     */
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }


    /**
     * Обрабатывает входящие обновления от Telegram API.
     * <p>
     * Этот метод вызывается при получении нового сообщения или callback-запроса.
     * В зависимости от входящих данных бот отправляет ответное сообщение или выполняет действие.
     * </p>
     *
     * @param update Объект, содержащий данные о новом событии.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleCommand(update);
        } else if (update.hasCallbackQuery()) {
            handleCallback(update);
        }
    }

    private void handleCommand(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        logger.info("Сообщение от пользователя {} (ID: {}): {}", update.getMessage().getFrom().getFirstName(), update.getMessage().getChatId(), update.getMessage().getText());
        Map<String, Runnable> commands = registerCommands(update);
        Optional.ofNullable(commands.get(messageText))
                .orElse(() -> sendMessage(chatId, DEFAULT.message))
                .run();
    }

    private void handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        logger.info("Callback от пользователя {} (ID: {}): {}",
                update.getCallbackQuery().getFrom().getFirstName(), chatId, callbackData);
        if (ValutaCharCode.contain(callbackData)) {
            sendTimeFrameMenu(chatId, messageId, callbackData);
            return;
        }

        CallbackData parsedData = CallbackData.parse(callbackData);
        if (parsedData == null) {
            logger.warn("Неизвестный callback: {}", callbackData);
            return;
        }

        switch (parsedData.action) {
            case ACTUAL -> editMessageWithRate(chatId, messageId, currencyService.getLatestRates(parsedData.currency));
            case WEEK, TWO_WEEKS, MONTH -> sendSecondTimeFrameMenu(chatId, messageId, callbackData);
            case TEXT ->
                    editMessageWithRate(chatId, messageId, currencyService.getRatesForPeriod(parsedData.currency, parsedData.getDays()));
            case CHART -> {
                File chart = currencyService.getChartRatesFromNow(parsedData.currency, parsedData.getDays());
                sendChart(String.valueOf(chatId), chart);
                editMessageWithRate(chatId, messageId, "Chart of " + parsedData.currency);
            }
            case OTHER_CURRENCY -> sendCharCodeMenu(chatId, messageId, callbackData);
            default -> logger.warn("Неизвестное действие: {}", parsedData.action);
        }

    }

    private Map<String, Runnable> registerCommands(Update update) {
        long chatId = update.getMessage().getChatId();
        Map<String, Runnable> commands = new HashMap<>();
        commands.put(GET_RATE.command, () -> startCommandReceived(chatId, GET_RATE.message));
        commands.put(GET_RATE.buttonName, () -> startCommandReceived(chatId, GET_RATE.message));
        commands.put(HELP.command, () -> sendMessage(chatId, HELP.message));
        commands.put(HELP.buttonName, () -> sendMessage(chatId, HELP.message));
        commands.put(ABOUT.command, () -> sendMessage(chatId, ABOUT.message));
        commands.put(ABOUT.buttonName, () -> sendMessage(chatId, ABOUT.message));
        commands.put(START.command, () -> startBot(chatId, update, START.message));
        commands.put(START.buttonName, () -> startBot(chatId, update, START.message));
        commands.put(DEFAULT.command, () -> sendMessage(chatId, DEFAULT.message));
        commands.put(DEFAULT.buttonName, () -> sendMessage(chatId, DEFAULT.message));
        return commands;
    }

    private void startCommandReceived(Long chatId, String text) {
        SendMessage message = getSendMessage(chatId, text);
        message.setReplyMarkup(telegramMenu.createCurrencyMenu());
        executeSafely(message);
    }

    private void startBot(long chatId, Update update, String startText) {
        executeSafely(telegramMenu.createBotCommands());
        SendMessage message = getSendMessage(chatId, String.format(startText, update.getMessage().getChat().getFirstName().toString()));
        message.setReplyMarkup(telegramMenu.createReplyKeyboard());
        executeSafely(message);
        startCommandReceived(chatId, GET_RATE.message);
    }

    private void sendMessage(long chatId, String text) {
        executeSafely(getSendMessage(chatId, text));
    }

    private static SendMessage getSendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    private static EditMessageText getEditMessageText(Long chatId, Integer messageId) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        return editMessage;
    }

    private void editMessageWithRate(Long chatId, Integer messageId, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.toString());
        editMessageText.setParseMode(ParseMode.HTML);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        executeSafely(editMessageText);
    }

    private void sendCharCodeMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = getEditMessageText(chatId, messageId);
        editMessage.setText("Choose period for " + currency + ":");
        editMessage.setReplyMarkup(telegramMenu.createCharCodeMenu());
        executeSafely(editMessage);
    }

    private void sendTimeFrameMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = getEditMessageText(chatId, messageId);
        editMessage.setText("Select period for " + currency + ":");
        editMessage.setReplyMarkup(telegramMenu.createTimeFrameMenu(currency));
        executeSafely(editMessage);
    }

    private void sendSecondTimeFrameMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = getEditMessageText(chatId, messageId);
        editMessage.setText("Select display option " + currency.split("_")[0] + ":");
        editMessage.setReplyMarkup(telegramMenu.createSecondTimeFrameMenu(currency));
        executeSafely(editMessage);
    }

    private void sendChart(String chatId, File chart) {
        executeSafely(new SendPhoto(chatId, new InputFile(chart)));
    }
}
