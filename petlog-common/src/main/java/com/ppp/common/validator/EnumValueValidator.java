package com.ppp.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private Enum<?>[] enumValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enumValues = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && Arrays.stream(enumValues)
                .anyMatch(enumValue -> enumValue.toString().equals(value));
    }
}
