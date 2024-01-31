package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.common.service.FileManageService;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryMedia;
import com.ppp.domain.diary.constant.DiaryMediaType;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ppp.api.diary.exception.ErrorCode.*;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;
import static com.ppp.domain.common.constant.Domain.DIARY;

@RequiredArgsConstructor
@Service
@Slf4j
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final PetRepository petRepository;
    private final GuardianRepository guardianRepository;
    private final FileManageService fileManageService;

    @Transactional
    public void createDiary(User user, Long petId, DiaryRequest request, List<MultipartFile> images) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        validateCreateDiary(pet, user);

        Diary diary = Diary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .user(user)
                .pet(pet)
                .build();
        diary.addDiaryMedias(uploadAndGetDiaryMedias(images, diary));
        diaryRepository.save(diary);
    }

    private void validateCreateDiary(Pet pet, User user) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    private List<DiaryMedia> uploadAndGetDiaryMedias(List<MultipartFile> images, Diary diary) {
        return fileManageService.uploadImages(images, DIARY).stream()
                .map(uploadedPath -> DiaryMedia.builder()
                        .path(uploadedPath)
                        .type(DiaryMediaType.IMAGE)
                        .diary(diary)
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public void updateDiary(User user, Long diaryId, DiaryRequest request, List<MultipartFile> images) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user, diary.getPet());

        deleteDiaryMedia(diary);
        diary.update(request.getTitle(), request.getContent(), request.getDate(),
                uploadAndGetDiaryMedias(images, diary));
    }

    private void validateModifyDiary(Diary diary, User user, Pet pet) {
        if (!Objects.equals(diary.getUser().getId(), user.getId()))
            throw new DiaryException(NOT_DIARY_OWNER);
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    private void deleteDiaryMedia(Diary diary) {
        fileManageService.deleteImages(diary.getDiaryMedias().stream().map(DiaryMedia::getPath)
                .collect(Collectors.toList()));
        diary.deleteDiaryMedias();
    }

    @Transactional
    public void deleteDiary(User user, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user, diary.getPet());

        deleteDiaryMedia(diary);
        diary.delete();
    }
}
