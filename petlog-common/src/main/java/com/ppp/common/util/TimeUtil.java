package com.ppp.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public static String calculateAge(LocalDateTime date){
        long day = ChronoUnit.DAYS.between(date, LocalDateTime.now());
        if((day /= DAY) < MONTH)
            return day + "개월";
        return day / MONTH + "살";
    }

    private TimeUtil() {
    }
}
