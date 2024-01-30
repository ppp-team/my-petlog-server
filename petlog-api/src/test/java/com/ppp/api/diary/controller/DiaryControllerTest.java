package com.ppp.api.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.service.DiaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DiaryController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiaryControllerTest {
    @MockBean
    DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "Bearer token";

    @Test
    @DisplayName("일기 생성 성공")
    void createDiary_success() throws Exception {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), new ArrayList<>());
        //when
        mockMvc.perform(multipart("/api/vi/pets/{petId}/diaries", 1L)
                        .file(new MockMultipartFile("request", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("images", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("videos", "video.mp4",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("일기 생성 성공-file not required")
    void createDiary_success_FILE_NOT_REQUIRED() throws Exception {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), new ArrayList<>());
        //when
        mockMvc.perform(multipart("/api/vi/pets/{petId}/diaries", 1L)
                        .file(new MockMultipartFile("request", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("일기 수정 성공")
    void updateDiary_success() throws Exception {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), new ArrayList<>());
        //when
        mockMvc.perform(multipart("/api/vi/pets/diaries/{diaryId}", 1L)
                        .file(new MockMultipartFile("request", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("images", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("videos", "video.mp4",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(httpServletRequest -> {
                            httpServletRequest.setMethod("PUT");
                            return httpServletRequest;
                        })
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("일기 수정 성공-file not required")
    void updateDiary_success_FILE_NOT_REQUIRED() throws Exception {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), new ArrayList<>());
        //when
        mockMvc.perform(multipart("/api/vi/pets/diaries/{diaryId}", 1L)
                        .file(new MockMultipartFile("request", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(httpServletRequest -> {
                            httpServletRequest.setMethod("PUT");
                            return httpServletRequest;
                        })
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("일기 삭제 성공")
    void deleteDiary_success() throws Exception {
        mockMvc.perform(delete("/api/vi/pets/diaries/{diaryId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }
}