package com.belnitskii.telegrambotcb.constant;

public enum TimeFrame {
    WEEK(7), TWO_WEEKS(14), MONTH(30);

    public final int days;

    TimeFrame(int days) {
        this.days = days;
    }
}
