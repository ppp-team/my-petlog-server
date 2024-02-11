package com.ppp.api.pet.service;

import com.ppp.api.pet.dto.request.PetRequest;
import com.ppp.api.pet.dto.response.MyPetResponse;
import com.ppp.api.pet.dto.response.MyPetsResponse;
import com.ppp.common.service.FileManageService;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.constant.RepStatus;
import com.ppp.domain.pet.repository.PetImageRepository;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetsServiceTest {
    @Mock
    private PetRepository petRepository;

    @Mock
    private PetImageRepository petImageRepository;

    @Mock
    private FileManageService fileManageService;

    @InjectMocks
    private PetsService petsService;

    private User user = null;
    private MockMultipartFile file = null;
    private Pet pet = null;

    @BeforeEach
    void init() {
        user = User.builder()
                .id("randomstring")
                .nickname("nickname")
                .build();
        file = new MockMultipartFile("petImage", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());
        pet = Pet.builder().id(1L).user(user).isNeutered(false).build();
    }

    @Test
    @DisplayName("반려동물 추가")
    void createPetTest() {
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

        petsService.createPet(petRequest, user, null);

        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("반려동물 수정")
    void updatePetTest() {
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

        when(petRepository.findMyPetById(1L, user.getId())).thenReturn(Optional.of(pet));

        petsService.updatePet(1L, petRequest, user, null);

        assertEquals("name", pet.getName());
    }

    @Test
    @DisplayName("반려동물 조회")
    void displayPetTest() {
        when(petRepository.findMyPetById(1L, user.getId())).thenReturn(Optional.of(pet));

        MyPetResponse myPetResponse = petsService.findMyPetById(1L, user);

        Assertions.assertThat(myPetResponse).isNotNull();
    }

    @Test
    @DisplayName("반려동물 리스트")
    void displayPetsTest() {
        List<Pet> myPets = new ArrayList<>();
        myPets.add(pet);

        when(petRepository.findAllByUserId(user.getId())).thenReturn(myPets);

        MyPetsResponse MyPetsResponse = petsService.findMyPets(user);

        Assertions.assertThat(MyPetsResponse).isNotNull();
    }

    @Test
    @DisplayName("대표 반려동물 지정")
    void selectRepresentativePet_switchFromNormalToRepresentative() {
        Pet pet1 = Pet.builder().id(1L).user(user).repStatus(RepStatus.REPRESENTATIVE).isNeutered(false).build();
        Pet pet2 = Pet.builder().id(2L).user(user).repStatus(RepStatus.NORMAL).isNeutered(false).build();

        when(petRepository.findRepresentativePet(user.getId())).thenReturn(Optional.of(pet1));
        when(petRepository.findMyPetById(2L,user.getId())).thenReturn(Optional.of(pet2));

        petsService.selectRepresentative(pet2.getId(), user);

        assertEquals(RepStatus.NORMAL, pet1.getRepStatus());
        assertEquals(RepStatus.REPRESENTATIVE, pet2.getRepStatus());
    }
}
