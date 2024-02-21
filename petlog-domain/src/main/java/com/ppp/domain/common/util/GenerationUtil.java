package com.ppp.domain.common.util;


import java.security.SecureRandom;

public class GenerationUtil {

    public static String generateCode() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
    }

    public static String generateIdFromEmail(String email) {
        String emailPrefix = getEmailPrefix(email);
        String randomDigits = generateRandomDigits();

        return emailPrefix + randomDigits;
    }

    private static String getEmailPrefix(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        } else {
            return email;
        }
    }

    private static String generateRandomDigits() {
        SecureRandom random = new SecureRandom();
        int randomInt = 1000 + random.nextInt(9000);
        return String.valueOf(randomInt);
    }
}