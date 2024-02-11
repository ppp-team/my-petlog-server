package com.ppp.api.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppp.api.pet.dto.request.PetRequest;
import com.ppp.api.pet.service.PetsService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetsController.class)
@AutoConfigureMockMvc(addFilters = false)
class PetsControllerTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetsService petsService;

    private static final String TOKEN = "Bearer token";

    @Test
    @DisplayName("반려동물 추가")
    @WithMockCustomUser
    void createPetTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("petImage", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());

        PetRequest petRequest = new PetRequest();
        petRequest.setName("name");
        petRequest.setType("type");
        petRequest.setBreed("breed");
        petRequest.setGender("MALE");
        petRequest.setIsNeutered(true);
        petRequest.setBirth(LocalDate.of(2023, 1, 1));
        petRequest.setFirstMeetDate(LocalDate.of(2022, 1, 1));
        petRequest.setWeight(5.0);
        petRequest.setRegisteredNumber("1234");

        mockMvc.perform(multipart("/api/v1/my/pets")
                        .file(file)
                        .file(new MockMultipartFile("petRequest", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(petRequest).getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("반려동물 수정")
    @WithMockCustomUser
    void updatePetTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("petImage", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());

        PetRequest petRequest = new PetRequest();
        petRequest.setName("name");
        petRequest.setType("type");
        petRequest.setBreed("breed");
        petRequest.setGender("MALE");
        petRequest.setIsNeutered(true);
        petRequest.setBirth(LocalDate.of(2023, 1, 1));
        petRequest.setFirstMeetDate(LocalDate.of(2022, 1, 1));
        petRequest.setWeight(5.0);
        petRequest.setRegisteredNumber("1234");

        mockMvc.perform(multipart("/api/v1/my/pets/{petId}", 1L)
                        .file(file)
                        .file(new MockMultipartFile("petRequest", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(petRequest).getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(httpServletRequest -> {
                            httpServletRequest.setMethod("PUT");
                            return httpServletRequest;
                        })
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("반려동물 조회")
    @WithMockCustomUser
    void displayPetTest() throws Exception {
        mockMvc.perform(
                get("/api/v1/my/pets/{petId}",1L)
        ).andDo(print()).andExpect(status().isOk());

    }

    @Test
    @DisplayName("반려동물 리스트")
    @WithMockCustomUser
    void displayPetsTest() throws Exception {
        mockMvc.perform(
                get("/api/v1/my/pets")
        ).andDo(print()).andExpect(status().isOk());

    }

    @Test
    @DisplayName("대표 반려동물 지정")
    @WithMockCustomUser
    void selectRepresentativeTest() throws Exception {
        mockMvc.perform(
                post("/api/v1/my/pets/{petId}/selectRep",1L)
        ).andDo(print()).andExpect(status().isOk());

    }
}
