package com.ppp.api.exception;


import com.ppp.api.mock.exception.MockException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";
    Logger defaultLog = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    Logger exceptionLog = LoggerFactory.getLogger("ExceptionLogger");

    @ExceptionHandler(MockException.class)
    public ResponseEntity<ExceptionResponse> handleMockException(MockException exception){
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException exception){
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        defaultLog.error(exception.getMessage());
        exceptionLog.error(exception.getMessage(), exception);
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("internal_server_error")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}