package com.ppp.api.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ExceptionResponse {
    private final int status;
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public ExceptionResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ExceptionResponse toErrorResponseDto() {
        return ExceptionResponse
                .builder()
                .status(this.status)
                .code(this.code)
                .message(this.message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}