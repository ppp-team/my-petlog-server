package com.ppp.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeUtilTest {
    @Test
    @DisplayName("기간 계산 성공")
    void calculateTerm_success() {
        //given
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime beforeYears = current.minusMonths(13);
        LocalDateTime beforeMonths = current.minusMonths(2);
        LocalDateTime beforeDays = current.minusDays(15);
        LocalDateTime beforeMinutes = current.minusMinutes(14);
        LocalDateTime beforeSeconds = current.minusSeconds(4);
        //when
        String year = TimeUtil.calculateTerm(beforeYears);
        String month = TimeUtil.calculateTerm(beforeMonths);
        String day = TimeUtil.calculateTerm(beforeDays);
        String minute = TimeUtil.calculateTerm(beforeMinutes);
        String second = TimeUtil.calculateTerm(beforeSeconds);
        //then
        assertTrue(year.contains("년"));
        assertTrue(month.contains("달"));
        assertTrue(day.contains("일"));
        assertTrue(minute.contains("분"));
        assertTrue(second.contains("초"));
    }

    @Test
    void calculateAge_success() {
        //given
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime beforeYears = current.minusMonths(12);
        LocalDateTime beforeMonths = current.minusMonths(1);
        //when
        String year = TimeUtil.calculateAge(beforeYears);
        String month = TimeUtil.calculateAge(beforeMonths);
        //then
        assertTrue(year.contains("살"));
        assertTrue(month.contains("개월"));
    }
}