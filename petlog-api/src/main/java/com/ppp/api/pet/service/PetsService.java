package com.ppp.api.pet.service;

import com.ppp.api.pet.dto.request.PetRequest;
import com.ppp.api.pet.dto.response.PetResponse;
import com.ppp.api.pet.dto.response.PetsResponse;
import com.ppp.api.pet.exception.ErrorCode;
import com.ppp.api.pet.exception.PetException;
import com.ppp.common.service.FileManageService;
import com.ppp.domain.common.GenerationUtil;
import com.ppp.domain.common.constant.Domain;
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
public class PetsService {
    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;
    private final FileManageService fileManageService;

    @Transactional
    public void createPet(PetRequest petRequest, User user, MultipartFile petImage) {
        String inviteCode = GenerationUtil.generateCode();

        Pet pet = Pet.builder()
                .user(user)
                .name(petRequest.getName())
                .type(petRequest.getType())
                .breed(petRequest.getBreed())
                .gender(petRequest.getToGender())
                .isNeutered(petRequest.getIsNeutered())
                .birth(petRequest.convertToBirthLocalDateTime())
                .firstMeetDate(petRequest.convertToFirstMeetDateLocalDateTime())
                .weight(petRequest.getWeight())
                .registeredNumber(petRequest.getRegisteredNumber())
                .repStatus(RepStatus.NORMAL)
                .isDeleted(false)
                .invitedCode(inviteCode)
                .build();

        petRepository.save(pet);

        // save image
        savePetImage(pet, petImage);
    }

    private void savePetImage(Pet pet, MultipartFile petImage) {
        if (petImage != null && !petImage.isEmpty()) {
            String savedPath = fileManageService.uploadImage(petImage, Domain.PET)
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

    public PetsResponse findMyPets(User user) {
        List<PetResponse> petResponseList = new ArrayList<>();
        List<Pet> myPets = petRepository.findAllByUserId(user.getId());
        for (Pet pet : myPets) {
            PetImage petImage = petImageRepository.findByPet(pet)
                    .orElse(new PetImage());

            PetResponse petResponse = PetResponse.from(pet, petImage);
            petResponseList.add(petResponse);
        }
        return new PetsResponse(petResponseList.size(), petResponseList);
    }

    public PetResponse findMyPetById(Long petId, User user) {
        Pet pet = petRepository.findMyPetById(petId, user.getId())
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
        PetImage petImage = petImageRepository.findByPet(pet).orElse(new PetImage());
        return PetResponse.from(pet, petImage);
    }

    @Transactional
    public void updatePet(Long petId, PetRequest petRequest, User user, MultipartFile petImage) {
        Pet pet = petRepository.findMyPetById(petId, user.getId())
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
        updateByRequest(pet, petRequest);

        // save image
        savePetImage(pet, petImage);

        // delete previous image
        petImageRepository.findByPet(pet).ifPresent(
                image -> fileManageService.deleteImage(image.getUrl()));
    }

    private void updateByRequest(Pet pet, PetRequest petRequest) {
        pet.setName(petRequest.getName());
        pet.setType(petRequest.getType());
        pet.setBreed(petRequest.getBreed());
        pet.setGender(petRequest.getToGender());
        pet.setNeutered(petRequest.getIsNeutered());
        pet.setBirth(petRequest.convertToBirthLocalDateTime());
        pet.setFirstMeetDate(petRequest.convertToFirstMeetDateLocalDateTime());
        pet.setWeight(petRequest.getWeight());
        pet.setRegisteredNumber(petRequest.getRegisteredNumber());
    }

    @Transactional
    public void selectRepresentative(Long petId, User user) {
        // 기존 REP 있다면 NORMAL 로 바꾸고
        petRepository.findRepresentativePet(user.getId()).ifPresent(pet -> {
                    pet.setRepStatus(RepStatus.NORMAL);
                    petRepository.save(pet);
        });

        Pet pet = petRepository.findMyPetById(petId, user.getId())
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
        pet.setRepStatus(RepStatus.REPRESENTATIVE);
    }
}
