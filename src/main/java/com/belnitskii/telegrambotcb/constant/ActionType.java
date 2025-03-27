package com.belnitskii.telegrambotcb.constant;

public enum ActionType {
    ACTUAL("Actual Rate", "_ACTUAL"),
    TEXT("Text Format", "_TEXT"),
    CHART("Chart Format", "_CHART"),
    WEEK("Week", "_WEEK"),
    TWO_WEEKS("Two Weeks", "_2WEEK"),
    MONTH("Month", "_MONTH"),
    OTHER_CURRENCY("Other Currency", "_OTHER");

    public final String buttonName;
    public final String callbackSuffix;

    ActionType(String buttonName, String callbackSuffix) {
        this.buttonName = buttonName;
        this.callbackSuffix = callbackSuffix;
    }
}
