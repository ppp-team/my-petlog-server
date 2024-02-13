package com.ppp.api.video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.test.WithMockCustomUser;
import com.ppp.api.video.service.VideoManageService;
import com.ppp.common.security.UserDetailsServiceImpl;
import com.ppp.common.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VideoController.class)
@AutoConfigureMockMvc(addFilters = false)
class VideoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private VideoManageService videoManageService;

    private static final String TOKEN = "Bearer token";

    @Test
    @WithMockCustomUser
    @DisplayName("비디오 업로드 성공")
    void uploadTempVideo_success() throws Exception {
        //given
        //when
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(new MockMultipartFile("video", "video.wmv", MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes()))
                        .param("domain", "DIARY")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
        //then
    }
}