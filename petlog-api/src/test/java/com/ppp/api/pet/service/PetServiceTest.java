package com.ppp.api.pet.service;

import com.ppp.api.guardian.service.GuardianService;
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
class PetServiceTest {
    @Mock
    private PetRepository petRepository;

    @Mock
    private PetImageRepository petImageRepository;

    @Mock
    private FileManageService fileManageService;

    @Mock
    private GuardianService guardianService;

    @InjectMocks
    private PetService petService;

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
        PetRequest petRequest = PetRequest.builder()
                .name("name")
                .type("type")
                .breed("breed")
                .gender("MALE")
                .isNeutered(true)
                .birth(LocalDate.of(2023, 1, 1))
                .firstMeetDate(LocalDate.of(2022, 1, 1))
                .weight(5.0)
                .registeredNumber("1234")
                .build();

        petService.createPet(petRequest, user, null);

        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("반려동물 수정")
    void updatePetTest() {
        PetRequest petRequest = PetRequest.builder()
                .name("name")
                .type("type")
                .breed("breed")
                .gender("MALE")
                .isNeutered(true)
                .birth(LocalDate.of(2023, 1, 1))
                .firstMeetDate(LocalDate.of(2022, 1, 1))
                .weight(5.0)
                .registeredNumber("1234")
                .build();

        when(petRepository.findMyPetById(1L, user.getId())).thenReturn(Optional.of(pet));

        petService.updatePet(1L, petRequest, user, null);

        assertEquals("name", pet.getName());
    }

    @Test
    @DisplayName("반려동물 조회")
    void displayPetTest() {
        when(petRepository.findMyPetById(1L, user.getId())).thenReturn(Optional.of(pet));

        MyPetResponse myPetResponse = petService.findMyPetById(1L, user);

        Assertions.assertThat(myPetResponse).isNotNull();
    }

    @Test
    @DisplayName("반려동물 리스트")
    void displayPetsTest() {
        List<Pet> myPets = new ArrayList<>();
        myPets.add(pet);

        when(petRepository.findAllByUserId(user.getId())).thenReturn(myPets);

        MyPetsResponse MyPetsResponse = petService.findMyPets(user);

        Assertions.assertThat(MyPetsResponse).isNotNull();
    }

    @Test
    @DisplayName("반려동물 삭제")
    void deleteMyPet() {
        Long petId = 1L;
        petService.deleteMyPet(petId, user);

        verify(petRepository, times(1)).deleteById(petId);
    }

    @Test
    @DisplayName("대표 반려동물 지정")
    void selectRepresentativePet_switchFromNormalToRepresentative() {
        Pet pet1 = Pet.builder().id(1L).user(user).repStatus(RepStatus.REPRESENTATIVE).isNeutered(false).build();
        Pet pet2 = Pet.builder().id(2L).user(user).repStatus(RepStatus.NORMAL).isNeutered(false).build();

        when(petRepository.findRepresentativePet(user.getId())).thenReturn(Optional.of(pet1));
        when(petRepository.findMyPetById(2L,user.getId())).thenReturn(Optional.of(pet2));

        petService.selectRepresentative(pet2.getId(), user);

        assertEquals(RepStatus.NORMAL, pet1.getRepStatus());
        assertEquals(RepStatus.REPRESENTATIVE, pet2.getRepStatus());
    }
}
