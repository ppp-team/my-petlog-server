package com.ppp.api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.auth.dto.request.RegisterRequest;
import com.ppp.api.auth.dto.request.SigninRequest;
import com.ppp.api.auth.service.AuthService;
import com.ppp.common.security.UserDetailsServiceImpl;
import com.ppp.common.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입")
    void signup() throws Exception {
        //given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("j2@gmail.com")
                .password("password")
                .build();
        String json = new ObjectMapper().writeValueAsString(registerRequest);

        //when
        mockMvc.perform(
                post("/api/v1/auth/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("로그인")
    void signin() throws Exception {
        //given
        SigninRequest signinRequest = SigninRequest.builder()
                .email("j2@gmail.com")
                .password("password")
                .build();
        String json = new ObjectMapper().writeValueAsString(signinRequest);

        //when
        mockMvc.perform(
                post("/api/v1/auth/signin")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        //given
        String accessToken = "accessToken";

        //when
        mockMvc.perform(
                post("/api/v1/auth/logout")
                .header("accessToken", accessToken)
        ).andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("토큰재발급")
    void refreshToken() throws Exception {
        //given
        String refreshToken = "refreshToken";

        //when
        mockMvc.perform(
                post("/api/v1/auth/refresh-token")
                        .header("refreshToken", refreshToken)
        ).andExpect(status().isOk());

    }
}