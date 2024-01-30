package com.ppp.common.util;

public class ApiResponse<T> {
    private String resultCode;
    private String message;
    private T data;

    public ApiResponse(String resultCode, String message, T data) {
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
    public static <T> ApiResponse<T> success(String resultCode, T data) {
        return new ApiResponse<>(resultCode, "성공", data);
    }

    // 편의 메서드: 실패 응답
    public static <T> ApiResponse<T> fail(String resultCode, String message) {
        return new ApiResponse<>(resultCode, message, null);
    }
}

