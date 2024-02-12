package com.ppp.api.exception;


import com.ppp.api.auth.exception.AuthException;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.log.exception.LogException;
import com.ppp.api.mock.exception.MockException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.user.exception.UserException;
import com.ppp.api.video.exception.VideoException;
import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.FileException;
import com.ppp.common.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @ExceptionHandler(VideoException.class)
    public ResponseEntity<ExceptionResponse> handleVideoException(VideoException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ExceptionResponse> handleFileException(FileException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(LogException.class)
    public ResponseEntity<ExceptionResponse> handleLogException(LogException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(ErrorCode.REQUEST_ARGUMENT_ERROR.getMessage());

        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .code(ErrorCode.REQUEST_ARGUMENT_ERROR.getCode())
                .status(ErrorCode.REQUEST_ARGUMENT_ERROR.getStatus().value())
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorCode.REQUEST_ARGUMENT_ERROR.getStatus());
    }

    @ExceptionHandler(DiaryException.class)
    public ResponseEntity<ExceptionResponse> handleDiaryException(DiaryException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(PetException.class)
    public ResponseEntity<ExceptionResponse> handlePetException(PetException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(MockException.class)
    public ResponseEntity<ExceptionResponse> handleMockException(MockException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getStatus())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(AuthException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ExceptionResponse> handleTokenException(TokenException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getErrorCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(exception.getHttpStatus().value())
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.warn(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        ExceptionResponse errorResponse = ExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(LOG_FORMAT, exception.getClass().getSimpleName(), errorResponse.getCode(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}