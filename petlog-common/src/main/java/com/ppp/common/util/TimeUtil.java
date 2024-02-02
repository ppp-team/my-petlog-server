package com.ppp.common.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {
    private static final int SEC = 60;
    private static final int MIN = 60;
    private static final int HOUR = 24;
    private static final int DAY = 30;
    private static final int MONTH = 12;

    public static String calculateTerm(LocalDateTime date) {
        long term = Duration.between(date, LocalDateTime.now()).getSeconds();
        if (term < SEC) {
            return term + "초";
        } else if ((term /= SEC) < MIN) {
            return term + "분";
        } else if ((term /= MIN) < HOUR) {
            return term + "시간";
        } else if ((term /= HOUR) < DAY) {
            return term + "일";
        } else if ((term /= DAY) < MONTH) {
            return term + "달";
        }
        return term / MONTH + "년";
    }

    private TimeUtil() {
    }
}
