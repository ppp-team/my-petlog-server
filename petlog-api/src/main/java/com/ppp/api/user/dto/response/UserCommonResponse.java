package com.ppp.api.user.dto.response;

public class UserCommonResponse<T> {
    private String resultCode;
    private String message;
    private T data;

    public UserCommonResponse(String resultCode, String message, T data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // 편의 메서드: 성공 응답
    public static <T> UserCommonResponse<T> success(String message, T data) {
        return new UserCommonResponse<>("success", message, data);
    }

    // 편의 메서드: 실패 응답
    public static <T> UserCommonResponse<T> fail(String message,  T data) {
        return new UserCommonResponse<>("fail", message, data);
    }
}

