package com.ppp.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeValidator implements ConstraintValidator<DateTime, String> {
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(DateTime constraintAnnotation) {
        dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDateTime.parse(value, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
