package com.ppp.domain.common;


import java.security.SecureRandom;

public class GenerationUtil {

    // Function to generate an 8-character code using alphabets (a-z) and numbers (0-9)
    public static String generateCode() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        // Generate each character of the code
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
    }

    // 이메일에서 @ 앞 부분과 랜덤 숫자 4자리를 조합하여 ID 생성하는 함수
    public static String generateIdFromEmail(String email) {
        // 이메일에서 @ 앞 부분 추출
        String emailPrefix = getEmailPrefix(email);

        // 랜덤 숫자 4자리 생성
        String randomDigits = generateRandomDigits();

        return emailPrefix + randomDigits;
    }

    // 이메일에서 @ 앞 부분 추출
    private static String getEmailPrefix(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        } else {
            // @가 없는 경우, 전체 이메일을 반환
            return email;
        }
    }

    // 랜덤 숫자 4자리 생성
    private static String generateRandomDigits() {
        SecureRandom random = new SecureRandom();
        int randomInt = 1000 + random.nextInt(9000); // 1000부터 9999까지의 난수 생성
        return String.valueOf(randomInt);
    }
}