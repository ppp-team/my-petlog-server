package com.ppp.common.security.jwt;

import com.ppp.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;


@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 인증 과정 실패 하거나, 인증헤더를 보내지 않게 되는 경우 401(UnAuthorized)
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String exception = (String)request.getAttribute("exception");
        ErrorCode errorCode;

        log.debug("log: exception: {} ", exception);

        /**
         * 토큰 없는 경우
         */
        if(exception == null) {
            errorCode = ErrorCode.NOT_FOUND_TOKEN;
            setResponse(response, errorCode);
            return;
        }

        /**
         * 토큰 만료된 경우
         */
        if(exception.equals(ErrorCode.EXPIRED_TOKEN.getCode())) {
            errorCode = ErrorCode.EXPIRED_TOKEN;
            setResponse(response, errorCode);
            return;
        }

        /**
         * 토큰 시그니처가 다른 경우
         */
        if(exception.equals(ErrorCode.INVALID_SIGNATURE.getCode())) {
            errorCode = ErrorCode.INVALID_SIGNATURE;
            setResponse(response, errorCode);
            return;
        }

        errorCode = ErrorCode.MALFORMED_TOKEN;
        setResponse(response, errorCode);
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().println("{ \"message\" : \"" + errorCode.getMessage()
                + "\", \"code\" : \"" +  errorCode.getCode()
                + "\", \"status\" : " + errorCode.getStatus().value()
                + ", \"timestamp\" : " + LocalDateTime.now()
                +"}");
    }
}
