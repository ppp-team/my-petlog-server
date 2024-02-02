package com.ppp.api.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.diary.dto.request.DiaryCommentRequest;
import com.ppp.api.diary.service.DiaryCommentService;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DiaryCommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiaryCommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private DiaryCommentService diaryCommentService;

    private static final String TOKEN = "Bearer token";

    @Test
    @WithMockCustomUser
    @DisplayName("일기 댓글 생성 성공")
    void createDiaryComment_success() throws Exception {
        //given
        DiaryCommentRequest request =
                new DiaryCommentRequest("오늘은 김밥을 먹었어요", List.of("abcde553", "qwerty1243"));
        //when
        mockMvc.perform(post("/api/v1/pets/diaries/{diaryId}/comments", 1L)
                        .content(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @WithMockCustomUser
    @DisplayName("일기 댓글 수정 성공")
    void updateDiaryComment_success() throws Exception {
        //given
        DiaryCommentRequest request =
                new DiaryCommentRequest("오늘은 김밥을 먹었어요", List.of("abcde553", "qwerty1243"));
        //when
        mockMvc.perform(put("/api/v1/pets/diaries/comments/{commentId}", 1L)
                        .content(objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @WithMockCustomUser
    @DisplayName("일기 댓글 삭제 성공")
    void deleteDiaryComment_success() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/v1/pets/diaries/comments/{commentId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }
}