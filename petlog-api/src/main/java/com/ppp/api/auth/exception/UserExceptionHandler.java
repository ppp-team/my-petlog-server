package com.ppp.api.auth.exception;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.user.exception.NotFoundUserException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = {"com.ppp.api.auth", "com.ppp.api.user"})
public class UserExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";
    Logger defaultLog = LoggerFactory.getLogger(UserExceptionHandler.class);
    Logger exceptionLog = LoggerFactory.getLogger("ExceptionLogger");

    @ExceptionHandler(SigninException.class)
    public ResponseEntity<ExceptionResponse> signinException(SigninException exception){
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ExistsEmailException.class)
    public ResponseEntity<ExceptionResponse> existsEmailException(ExistsEmailException exception){
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ExceptionResponse> notFoundUserException(NotFoundUserException exception){
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}