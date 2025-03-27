package com.belnitskii.telegrambotcb;

import com.belnitskii.telegrambotcb.constant.ActionType;
import com.belnitskii.telegrambotcb.constant.TimeFrame;

public class CallbackData {
    public final TimeFrame timeFrame;
    public final ActionType action;
    public final String currency;

    private CallbackData(String currency, ActionType action, TimeFrame timeFrame) {
        this.currency = currency;
        this.action = action;
        this.timeFrame = timeFrame;
    }

    public static CallbackData parse(String callbackData) {
        String[] parts = callbackData.split("_");
        if (parts.length < 2) return null;

        String currency = parts[0];
        String actionStr = parts[parts.length - 1];
        String timeFrameStr = parts.length > 2 ? parts[parts.length - 2] : null;

        ActionType action = findActionType(actionStr);
        TimeFrame timeFrame = findTimeFrame(timeFrameStr);

        return action != null ? new CallbackData(currency, action, timeFrame) : null;
    }

    private static ActionType findActionType(String actionStr) {
        for (ActionType action : ActionType.values()) {
            if (action.callbackSuffix.equals("_" + actionStr)) {
                return action;
            }
        }
        return null;
    }

    private static TimeFrame findTimeFrame(String timeFrameStr) {
        if (timeFrameStr == null) return null;
        return switch (timeFrameStr) {
            case "WEEK" -> TimeFrame.WEEK;
            case "2WEEK" -> TimeFrame.TWO_WEEKS;
            case "MONTH" -> TimeFrame.MONTH;
            default -> null;
        };
    }

    public int getDays() {
        return timeFrame != null ? timeFrame.days : 0;
    }
}

