package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.ppp.api.diary.exception.ErrorCode.DIARY_NOT_FOUND;
import static com.ppp.api.diary.exception.ErrorCode.NOT_DIARY_OWNER;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final PetRepository petRepository;

    @Transactional
    public void createDiary(User user, Long petId, DiaryRequest request) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        //TODO: check user has authority on pet space
        //TODO: upload files to S3
        diaryRepository.save(Diary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .user(user)
                .pet(pet)
                .build());
    }

    @Transactional
    public void updateDiary(User user, Long diaryId, DiaryRequest request) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        //TODO: check user has authority on pet space
        //TODO: upload files to S3
        diary.update(request.getTitle(), request.getContent(), request.getDate());
    }

    private void validateModifyDiary(Diary diary, User user) {
        if (Objects.equals(diary.getUser().getId(), user.getId()))
            throw new DiaryException(NOT_DIARY_OWNER);
    }

    public void deleteDiary(User user, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        //TODO: check user has authority on pet space
        validateModifyDiary(diary, user);
        diary.delete();
    }
}
