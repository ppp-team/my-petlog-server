package com.ppp.api.pet.service;

import com.ppp.api.guardian.service.GuardianService;
import com.ppp.api.pet.dto.request.PetRequest;
import com.ppp.api.pet.dto.response.MyPetResponse;
import com.ppp.api.pet.dto.response.MyPetsResponse;
import com.ppp.api.pet.exception.ErrorCode;
import com.ppp.api.pet.exception.PetException;
import com.ppp.common.service.FileStorageManageService;
import com.ppp.domain.common.util.GenerationUtil;
import com.ppp.domain.common.constant.Domain;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.guardian.dto.MyPetResponseDto;
import com.ppp.domain.guardian.repository.GuardianQuerydslRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.PetImage;
import com.ppp.domain.pet.constant.RepStatus;
import com.ppp.domain.pet.repository.PetImageRepository;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;
    private final FileStorageManageService fileStorageManageService;
    private final GuardianService guardianService;
    private final GuardianQuerydslRepository guardianQuerydslRepository;

    @Transactional
    public void createPet(PetRequest petRequest, User user, MultipartFile petImage) {
        String inviteCode = GenerationUtil.generateCode();

        Pet pet = Pet.builder()
                .user(user)
                .name(petRequest.getName())
                .type(petRequest.getType())
                .breed(petRequest.getBreed())
                .gender(petRequest.getToGender())
                .isNeutered(petRequest.getIsNeutered() != null ? petRequest.getIsNeutered() : null)
                .birth(petRequest.convertToBirthLocalDateTime())
                .firstMeetDate(petRequest.convertToFirstMeetDateLocalDateTime())
                .weight(petRequest.getWeight())
                .registeredNumber(petRequest.getRegisteredNumber())
                .repStatus(RepStatus.NORMAL)
                .isDeleted(false)
                .invitedCode(inviteCode)
                .build();

        Pet savedPet = petRepository.save(pet);
        guardianService.createGuardian(savedPet, user, GuardianRole.LEADER);

        savePetImage(pet, petImage);
    }

    private void savePetImage(Pet pet, MultipartFile petImage) {
        if (petImage != null && !petImage.isEmpty()) {
            String savedPath = fileStorageManageService.uploadImage(petImage, Domain.PET)
                    .orElseThrow(() -> new PetException(ErrorCode.PET_IMAGE_REGISTRATION_FAILED));
            uploadPetImage(pet, savedPath);
        }
    }

    private void uploadPetImage(Pet pet, String path) {
        Optional<PetImage> existingImage = petImageRepository.findByPet(pet);
        if (existingImage.isPresent()) {
            PetImage image = existingImage.get();
            image.setUrl(path);
        } else {
            PetImage newImage = PetImage.builder().pet(pet).url(path).build();
            petImageRepository.save(newImage);
        }
    }

    public MyPetsResponse findMyPetByInGuardian(User user) {
        List<MyPetResponse> myPetResponseList = new ArrayList<>();

        List<MyPetResponseDto> myPetResponseDtos = guardianQuerydslRepository.findMyPetByInGuardian(user.getId());
        myPetResponseDtos.forEach(
                myPetResponseDto -> myPetResponseList.add(MyPetResponse.from(myPetResponseDto))
        );

        return new MyPetsResponse(myPetResponseList.size(), myPetResponseList);
    }

    public MyPetResponse findMyPetById(Long petId, User user) {
        Pet pet = petRepository.findMyPetById(petId, user.getId())
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
        PetImage petImage = petImageRepository.findByPet(pet).orElse(new PetImage());
        return MyPetResponse.from(pet, petImage);
    }

    public String findPetCode(Long petId) {
        return petRepository.findPetCodeById(petId).orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }

    @Transactional
    public void updatePet(Long petId, PetRequest petRequest, User user, MultipartFile petImage) {
        Pet pet = petRepository.findMyPetById(petId, user.getId())
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

        petImageRepository.findByPet(pet).ifPresent(
                image -> fileStorageManageService.deleteImage(image.getUrl()));

        pet.updatePet(petRequest.getName(), petRequest.getType(), petRequest.getBreed(), petRequest.getGender()
                , petRequest.getIsNeutered(), petRequest.getBirth(), petRequest.getFirstMeetDate(), petRequest.getWeight(), petRequest.getRegisteredNumber());

        savePetImage(pet, petImage);
    }

    @Transactional
    public void selectRepresentative(Long petId, User user) {
        // 기존 REP 있다면 NORMAL 로 바꿉니다.
        petRepository.findRepresentativePet(user.getId()).ifPresent(pet -> {
            pet.updateRepStatus(RepStatus.NORMAL);
            petRepository.save(pet);
        });

        Pet pet = petRepository.findMyPetById(petId, user.getId())
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
        pet.updateRepStatus(RepStatus.REPRESENTATIVE);
    }

    @Transactional
    public void deleteMyPet(Long petId, User user) {
        Guardian guardian = guardianService.findByUserIdAndPetId(user, petId);
        guardianService.deleteReaderGuardian(guardian, petId);

        petRepository.deleteById(petId);
    }
}
