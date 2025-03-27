package com.belnitskii.telegrambotcb.constant;

public enum TelegramCommands {
    START("/start", "Start", generateStartMessage(), "Start the bot \uD83D\uDE80"),
    GET_RATE("/get", "Get rate", "Select currency:", "CB exchange rates \uD83D\uDCB0"),
    ABOUT("/about", "About program", "Something about the program, I'll add it later))", "Bot information ℹ\uFE0F"),
    DEFAULT("/default", "Default", "Unknown command. Type /help to get a list of available commands..", "Unknown command"),
    HELP("/help", "Help", generateHelpMessage(), "Show commands ℹ\uFE0F ");

    public final String command;
    public final String buttonName;
    public final String message;
    public final String description;

    TelegramCommands(String command, String buttonName, String message, String description) {
        this.command = command;
        this.buttonName = buttonName;
        this.message = message;
        this.description = description;
    }

    private static String generateHelpMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("This bot shows exchange rates.\nAvailable commands:\n")
                .append(START.command + " — " + START.description + "\n")
                .append(GET_RATE.command + " — " + GET_RATE.description + "\n")
                .append(ABOUT.command + " — " + ABOUT.description);
        return sb.toString();
    }

    private static String generateStartMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Welcome to the Exchange Rate Bot! \uD83D\uDCB1\uD83D\uDCCA\n" +
                "\n" +
                "Hello, %s! \uD83D\uDC4B I am a Telegram bot that provides up-to-date exchange rates based on data from the Central Bank of Russia. Here’s what I can do:\n" +
                "\n" +
                "\uD83D\uDD39 Get the latest exchange rates – I always provide the most recent rate available, whether it's for today or tomorrow (if the Central Bank has already published it).\n" +
                "\uD83D\uDD39 Check historical rates – View exchange rates for any period (week, two weeks, or a month).\n" +
                "\uD83D\uDD39 Support for all currencies listed by the Central Bank of Russia – Simply choose the one you need.\n" +
                "\uD83D\uDD39 Text or chart format – Get exchange rate data in a convenient text format or as a visual graph.\n" +
                "\n" +
                "To see the list of available commands, type /help. Let’s get started! \uD83D\uDE80");
        return sb.toString();
    }
}
