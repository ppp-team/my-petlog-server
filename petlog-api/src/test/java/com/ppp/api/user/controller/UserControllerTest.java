package com.ppp.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.auth.service.AuthService;
import com.ppp.api.user.dto.request.CheckRequest;
import com.ppp.api.user.service.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("닉네임중복검사")
    void checkNickname() throws Exception {
        //given
        CheckRequest checkRequest = new CheckRequest();
        checkRequest.setNickname("닉네임");
        String json = new ObjectMapper().writeValueAsString(checkRequest);

        mockMvc.perform(
                post("/api/v1/users/check/nickname")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk());

    }

    @Test
    @DisplayName("이메일중복검사")
    void checkEmail() throws Exception {
        //given
        CheckRequest checkRequest = new CheckRequest();
        checkRequest.setEmail("a@naver.com");
        String json = new ObjectMapper().writeValueAsString(checkRequest);

        mockMvc.perform(
                post("/api/v1/users/check/email")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk());

    }
}
