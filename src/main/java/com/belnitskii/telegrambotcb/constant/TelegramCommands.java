package com.belnitskii.telegrambotcb.constant;

public enum TelegramCommands {
    START("/start", "Start", "Hello %s! I am a Telegram bot that shows exchange rates."),
    GET_RATE("/get", "Get rate", "Select currency:"),
    ABOUT("/about", "About program", "Something about the program, I'll add it later))"),
    DEFAULT("/default", "Default", "Unknown command. Type /help to get a list of available commands.."),
    HELP("/help", "Help", generateHelpText());

    public final String command;
    public final String buttonName;
    public final String message;

    TelegramCommands(String command, String buttonName, String message) {
        this.command = command;
        this.buttonName = buttonName;
        this.message = message;
    }

    private static String generateHelpText() {
        StringBuilder sb = new StringBuilder();
        sb.append("This bot shows exchange rates.\nAvailable commands:\n")
                .append(START.command + " — start\n")
                .append(GET_RATE.command + " — get rate\n")
                .append(ABOUT.command + " — about the program");
        return sb.toString();
    }
}
