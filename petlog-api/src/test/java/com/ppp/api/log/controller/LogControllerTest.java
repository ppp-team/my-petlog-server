package com.ppp.api.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.service.LogService;
import com.ppp.api.test.WithMockCustomUser;
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

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private LogService logService;

    private static final String TOKEN = "Bearer token";

    @Test
    @WithMockCustomUser
    @DisplayName("건강 기록 생성 성공")
    void createLog_success() throws Exception {
        //given
        LogRequest request = new LogRequest("CUSTOM", "강아지 카페 가기", "2024-02-02T11:11", true, true, "고구마 챙겨가기", "abcde");

        //when
        mockMvc.perform(post("/api/v1/pets/{petId}/logs", 1L)
                        .content(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @WithMockCustomUser
    @DisplayName("건강 기록 생성 실패-잘못된 type")
    void createLog_fail_WhenTypeIsWrong() throws Exception {
        //given
        LogRequest request = new LogRequest("CUSTO", "강아지 카페 가기", "2024-02-02T11:11", true, true, "고구마 챙겨가기", "abcde");

        //when
        mockMvc.perform(post("/api/v1/pets/{petId}/logs", 1L)
                        .content(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isBadRequest());
        //then
    }

    @Test
    @WithMockCustomUser
    @DisplayName("건강 기록 생성 실패-잘못된 datetime")
    void createLog_fail_WhenDatetimeIsWrong() throws Exception {
        //given
        LogRequest request = new LogRequest("CUSTOM", "강아지 카페 가기", "2024-02-02T11:1", true, true, "고구마 챙겨가기", "abcde");

        //when
        mockMvc.perform(post("/api/v1/pets/{petId}/logs", 1L)
                        .content(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isBadRequest());
        //then
    }

    @Test
    @WithMockCustomUser
    @DisplayName("건강 기록 수정 성공")
    void updateLog_success() throws Exception {
        //given
        LogRequest request = new LogRequest("CUSTOM", "강아지 카페 가기", "2024-02-02T11:11", true, true, "고구마 챙겨가기", "abcde");

        //when
        mockMvc.perform(put("/api/v1/pets/{petId}/logs/{logId}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @WithMockCustomUser
    @DisplayName("건강 기록 삭제 성공")
    void deleteLog_success() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/v1/pets/{petId}/logs/{logId}", 1L, 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }
}