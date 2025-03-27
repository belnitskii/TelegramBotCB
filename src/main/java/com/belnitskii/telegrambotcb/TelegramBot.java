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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.*;

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

    private void handleCommand(Update update) {
        String messageText = update.getMessage().getText().toLowerCase();
        long chatId = update.getMessage().getChatId();

        logger.info("Сообщение от пользователя {} (ID: {}): {}", update.getMessage().getFrom().getFirstName(), update.getMessage().getChatId(), update.getMessage().getText());

        Map<String, Runnable> commands = new HashMap<>();
        commands.put("/get", () -> startCommandReceived(chatId));
        commands.put("get rate", () -> startCommandReceived(chatId));
        commands.put("/help", () -> sendHelpMessage(chatId));
        commands.put("help", () -> sendHelpMessage(chatId));
        commands.put("/about", () -> sendMessage(chatId, "Something about the program, I'll add it later))"));
        commands.put("about program", () -> sendMessage(chatId, "Something about the program, I'll add it later))"));
        commands.put("/start", () -> startBot(chatId, update));
        commands.put("start bot", () -> startBot(chatId, update));

        commands.getOrDefault(messageText, () -> sendMessage(chatId, "Unknown command. Type /help to get a list of available commands..")).run();
    }
    private void sendHelpMessage(long chatId) {
        sendMessage(chatId, "This bot shows exchange rates. Available commands:\n" +
                "/help - help\n" +
                "/start - start\n" +
                "/get - get rate\n" +
                "/about - about the program");
    }

    private void startBot(long chatId, Update update) {
        setBotCommands();
        sendMessageWithKeyboard(chatId, "Hello " + update.getMessage().getChat().getFirstName() + "! I am a Telegram bot that shows exchange rates.");
        startCommandReceived(chatId);
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

    private void handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        logger.info("Callback от пользователя {} (ID: {}): {}",
                update.getCallbackQuery().getFrom().getFirstName(), chatId, callbackData);

        if (ValutaCharCode.contain(callbackData)) {
            sendTimeFrameMenu(chatId, messageId, callbackData);
        }
        if (callbackData.equals("Other Currency")) {
            sendCharCodeMenu(chatId, messageId, callbackData);
        }
        if (callbackData.endsWith("_ACTUAL")) {
            String currency = callbackData.split("_")[0];
            String rate;
            rate = currencyService.getLatestRates(currency);
            editMessageWithRate(chatId, messageId, rate);
        }
        if (callbackData.endsWith("_WEEK")) {
            sendSecondTimeFrameMenu(chatId, messageId, callbackData);
        }
        if (callbackData.endsWith("_TEXT")) {
            String currency = callbackData.split("_")[0];
            String rate;
            rate = currencyService.getRatesForPeriod(currency, 7);
            editMessageWithRate(chatId, messageId, rate);
        }
        if (callbackData.endsWith("_CHART")) {
            String currency = callbackData.split("_")[0];
            File chart = currencyService.getChartRatesFromNow(currency, 7);
            sendChart(String.valueOf(chatId), chart);
            editMessageWithRate(chatId, messageId, "Chart of " + currency);
        }
    }

    /**
     * Отправляет меню выбора валют.
     *
     * @param chatId ID чата, куда отправить сообщение.
     */
    private void startCommandReceived(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Select currency:");
        message.setReplyMarkup(getCurrencyMenu());
        executeSafely(message);
    }

    /**
     * Отправляет пользователю inline-клавиатуру всей доступной для выбора валюты.
     *
     * @param chatId    ID чата.
     * @param messageId ID сообщения, которое будет редактироваться.
     * @param currency  Код валюты.
     */
    private void sendCharCodeMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText("Choose period for " + currency + ":");
        editMessage.setReplyMarkup(telegramMenu.createCharCodeMenu());
        executeSafely(editMessage);
    }

    /**
     * Отправляет пользователю меню выбора временного интервала для отображения курса валют.
     *
     * @param chatId    ID чата пользователя.
     * @param messageId ID сообщения, которое будет редактироваться.
     * @param currency  Код валюты.
     */
    private void sendTimeFrameMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText("Select period for " + currency + ":");
        editMessage.setReplyMarkup(telegramMenu.createTimeFrameMenu(currency));
        executeSafely(editMessage);
    }

    /**
     * Отправляет пользователю меню выбора формата отображения курса валют за неделю, текстом или в виде графика.
     *
     * @param chatId    ID чата пользователя.
     * @param messageId ID сообщения, которое будет редактироваться.
     * @param currency  Код валюты с суффиксом "_WEEK".
     */
    private void sendSecondTimeFrameMenu(Long chatId, Integer messageId, String currency) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText("Select display option " + currency.split("_")[0] + ":");
        editMessage.setReplyMarkup(telegramMenu.createSecondTimeFrameMenu(currency));
        executeSafely(editMessage);
    }

    /**
     * Создает inline-клавиатуру с основными валютами и возможностью выбора других.
     *
     * @return {@link InlineKeyboardMarkup} с кнопками выбора валюты.
     */
    private InlineKeyboardMarkup getCurrencyMenu() {
        return telegramMenu.createCurrencyMenu();
    }

    /**
     * Создает клавиатуру для отправки пользователю с основными командами.
     *
     * @return {@link ReplyKeyboardMarkup} с кнопками для управления ботом.
     */
    private ReplyKeyboardMarkup getReplyKeyboard() {
        return telegramMenu.createReplyKeyboard();
    }

    /**
     * Редактирует сообщение, добавляя в него новый текст с полученным курсом валют.
     *
     * @param chatId    ID чата.
     * @param messageId ID сообщения, которое нужно отредактировать.
     * @param newText   Новый текст сообщения.
     */
    private void editMessageWithRate(Long chatId, Integer messageId, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setParseMode(ParseMode.HTML);
        editMessageText.setChatId(chatId.toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        executeSafely(editMessageText);
    }

    private void setBotCommands() {
        executeSafely(telegramMenu.createBotCommands());
    }

    /**
     * Отправляет изображение (график курса валют) пользователю.
     *
     * @param chatId ID чата.
     * @param chart  Файл с изображением графика.
     */
    private void sendChart(String chatId, File chart) {
        executeSafely(new SendPhoto(chatId, new InputFile(chart)));
    }

    /**
     * Отправляет текстовое сообщение пользователю.
     *
     * @param chatId ID чата.
     * @param text   Текст сообщения.
     */
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setParseMode(ParseMode.HTML);
        message.setChatId(chatId);
        message.setText(text);
        executeSafely(message);
    }

    /**
     * Отправляет текстовое сообщение с клавиатурой.
     * Использую при выполнении команды /start, пользователь получает приветственное сообщение
     * и кнопки ReplyKeyboardMarkup меню {@link #getReplyKeyboard()}.
     *
     * @param chatId ID чата.
     * @param text   Текст сообщения.
     */
    private void sendMessageWithKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(getReplyKeyboard());
        executeSafely(message);
    }


}

