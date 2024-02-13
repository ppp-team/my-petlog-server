package com.ppp.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.auth.service.AuthService;
import com.ppp.api.test.WithMockCustomUser;
import com.ppp.api.user.dto.request.EmailRequest;
import com.ppp.api.user.dto.request.NicknameRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final String TOKEN = "Bearer token";

    @Test
    @DisplayName("닉네임중복검사")
    void checkNickname() throws Exception {
        //given
        NicknameRequest checkRequest = new NicknameRequest();
        checkRequest.setNickname("닉네임");
        String json = new ObjectMapper().writeValueAsString(checkRequest);

        //when
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
        EmailRequest checkRequest = new EmailRequest();
        checkRequest.setEmail("a@naver.com");
        String json = new ObjectMapper().writeValueAsString(checkRequest);

        //when
        mockMvc.perform(
                post("/api/v1/users/check/email")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk());

    }

    @Test
    @DisplayName("프로필 등록")
    @WithMockCustomUser
    void createProfile() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("profileImage", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());
        //when
        mockMvc.perform(multipart("/api/v1/users/profile",1L,1L)
                .file(file)
                .param("nickname","닉네임")
                .header("Authorization", TOKEN)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
        //then

    }

    @Test
    @DisplayName("프로필 수정")
    @WithMockCustomUser
    void updateProfile() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("profileImage", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());
        //when
        mockMvc.perform(multipart("/api/v1/users/profile",1L,1L)
                        .file(file)
                        .param("nickname","새로운닉네임")
                        .param("password","새로운비밀번호")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
        //then

    }

    @Test
    @DisplayName("내 정보 조회")
    @WithMockCustomUser
    void displayMe() throws Exception {
        //given

        //then
        mockMvc.perform(
                get("/api/v1/users/me")
        ).andDo(print()).andExpect(status().isOk());

    }
}
