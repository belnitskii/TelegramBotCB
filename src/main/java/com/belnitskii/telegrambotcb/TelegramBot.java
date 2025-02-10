package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.config.BotConfig;
import com.belnitskii.telegrambotcb.constant.ValutaCharCode;
import com.belnitskii.telegrambotcb.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Основной класс Telegram-бота, обрабатывающий входящие сообщения и команды.
 * <p>
 * Этот класс отвечает за взаимодействие с Telegram API, обработку пользовательских команд
 * и отправку ответов пользователям.
 * </p>
 */
@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig botConfig;
    private final CurrencyService currencyService;

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
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Сообщение от пользователя {} (ID: {}): {}", update.getMessage().getFrom().getFirstName(), update.getMessage().getChatId(), update.getMessage().getText());

            switch (messageText) {
                case "Курс валюты":
                    startCommandReceived(chatId);
                    break;
                case "/help", "Помощь":
                    sendMessage(chatId, "Этот бот показывает курсы валют. Доступные команды:\n/help - помощь\n/start - старт\n/about - о программе");
                    break;
                case "/about", "О программе":
                    sendMessage(chatId, "Что-то о программе, потом добавлю");
                    break;
                case "/start":
                    sendMessageWithKeyboard(chatId, "Здравствуйте " + update.getMessage().getChat().getFirstName() + "! Я Telegram-бот, который показывает курсы валют.");
                    startCommandReceived(chatId);
                    break;
                default:
                    sendMessage(chatId, "Неизвестная команда. Введите /help для получения списка доступных команд.");
            }
        } else if (update.hasCallbackQuery()) {
            logger.info("Пользователь {} (ID: {}):  {}",update.getCallbackQuery().getFrom().getFirstName(), update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getData());
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            if (ValutaCharCode.Contain(callbackData)) {
                sendTimeFrameMenu(chatId, messageId, callbackData);
            }
            if (callbackData.equals("Другая Валюта")) {
                sendCharCodeMenu(chatId, messageId, callbackData);
            }
            if (callbackData.endsWith("_TODAY")) {
                String currency = callbackData.split("_")[0];
                String rate;
                rate = currencyService.getCurrencyRate(currency);
                editMessageWithRate(chatId, messageId, rate);
            }
            if (callbackData.endsWith("_WEEK")) {
                sendSecondTimeFrameMenu(chatId, messageId, callbackData);
            }
            if (callbackData.endsWith("_TEXT")) {
                String currency = callbackData.split("_")[0];
                String rate;
                rate = currencyService.getWeekCurrencyRate(currency);
                editMessageWithRate(chatId, messageId, rate);
            }
            if (callbackData.endsWith("_CHART")) {
                String currency = callbackData.split("_")[0];
                File chart = currencyService.getWeekChartCurrencyRate(currency);
                sendChart(String.valueOf(chatId), chart);
                editMessageWithRate(chatId, messageId, "Вот ваш график");
            }
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
        message.setText("Выберите валюту:");
        message.setReplyMarkup(createCurrencyMenu());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения startCommandReceived", e);
        }
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
        editMessage.setText("Выберите период для " + currency + ":");
        editMessage.setReplyMarkup(createCharCodeMenu());
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения sendCharCodeMenu", e);
        }
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
        editMessage.setText("Выберите период для " + currency + ":");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Сегодня").callbackData(currency + "_TODAY").build());
        row.add(InlineKeyboardButton.builder().text("За неделю").callbackData(currency + "_WEEK").build());
        buttons.add(row);

        keyboardMarkup.setKeyboard(buttons);
        editMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения sendTimeFrameMenu", e);
        }
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
        editMessage.setText("Выберите удобный вариант отображения " + currency.split("_")[0] + ":");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Текст").callbackData(currency + "_TEXT").build());
        row.add(InlineKeyboardButton.builder().text("График").callbackData(currency + "_CHART").build());
        buttons.add(row);

        keyboardMarkup.setKeyboard(buttons);
        editMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения sendSecondTimeFrameMenu", e);
        }
    }

    /**
     * Создает inline-клавиатуру с доступными валютами.
     *
     * @return {@link InlineKeyboardMarkup} с кнопками выбора валюты.
     */
    private InlineKeyboardMarkup createCharCodeMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (ValutaCharCode charCode : ValutaCharCode.values()) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(charCode.name() + " — " + charCode.getName())
                    .callbackData(charCode.name())
                    .build();
            List<InlineKeyboardButton> list = new ArrayList<>();
            list.add(button);
            buttons.add(list);
        }

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    /**
     * Создает inline-клавиатуру с основными валютами и возможностью выбора других.
     *
     * @return {@link InlineKeyboardMarkup} с кнопками выбора валюты.
     */
    private InlineKeyboardMarkup createCurrencyMenu() {
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
                .text("Другая Валюта")
                .callbackData("Другая Валюта")
                .build());
        buttons.add(row2);

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    /**
     * Создает клавиатуру для отправки пользователю с основными командами.
     *
     * @return {@link ReplyKeyboardMarkup} с кнопками для управления ботом.
     */
    private ReplyKeyboardMarkup getReplyKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Курс валюты"));
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Помощь"));
        row2.add(new KeyboardButton("О программе"));
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
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
        editMessageText.setChatId(chatId.toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения editMessageWithRate", e);
        }
    }

    /**
     * Устанавливает команды бота в Telegram.
     * <p>
     * Команды включают: старт, помощь и информацию о боте.
     * </p>
     */
    public void setBotCommands() {
        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Запуск бота"),
                new BotCommand("/help", "Помощь"),
                new BotCommand("/about", "О программе")
        );

        SetMyCommands setMyCommands = new SetMyCommands(commands, new BotCommandScopeDefault(), null);

        try {
            execute(setMyCommands);
        } catch (Exception e) {
            logger.error("Ошибка выполнения setBotCommands", e);
        }
    }


    /**
     * Отправляет изображение (график курса валют) пользователю.
     *
     * @param chatId ID чата.
     * @param chart  Файл с изображением графика.
     */
    private void sendChart(String chatId, File chart) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(chart));

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            logger.error("Ошибка выполнения sendChart", e);
        }
    }

    /**
     * Отправляет текстовое сообщение пользователю.
     *
     * @param chatId ID чата.
     * @param text   Текст сообщения.
     */
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (Exception e) {
            logger.error("Ошибка выполнения sendMessage", e);
        }
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

        try {
            execute(message);
        } catch (Exception e) {
            logger.error("Ошибка выполнения sendMessageWithKeyboard", e);
        }
    }
}

