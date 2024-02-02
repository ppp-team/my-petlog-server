package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.dto.response.DiaryDetailResponse;
import com.ppp.api.diary.dto.response.DiaryGroupByDateResponse;
import com.ppp.api.diary.dto.response.DiaryResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final DiaryCommentCountRedisService diaryCommentCountService;

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
        diary.addDiaryMedias(uploadImagesAndGetDiaryMedias(images, diary));
        diaryRepository.save(diary);
        diaryCommentCountService.setDiaryCommentCountByDiaryId(diary.getId());
    }

    private void validateCreateDiary(Pet pet, User user) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    private List<DiaryMedia> uploadImagesAndGetDiaryMedias(List<MultipartFile> images, Diary diary) {
        if (images == null || images.isEmpty())
            return new ArrayList<>();
        return fileManageService.uploadImages(images, DIARY).stream()
                .map(uploadedPath -> DiaryMedia.of(diary, uploadedPath, DiaryMediaType.IMAGE))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateDiary(User user, Long diaryId, DiaryRequest request, List<MultipartFile> images) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user);

        deleteDiaryMedia(diary);
        diary.update(request.getTitle(), request.getContent(), request.getDate(),
                uploadImagesAndGetDiaryMedias(images, diary));
    }

    private void validateModifyDiary(Diary diary, User user) {
        if (!Objects.equals(diary.getUser().getId(), user.getId()))
            throw new DiaryException(NOT_DIARY_OWNER);
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), diary.getPet().getId()))
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
        validateModifyDiary(diary, user);

        deleteDiaryMedia(diary);
        diary.delete();
        diaryCommentCountService.deleteDiaryCommentCountByDiaryId(diaryId);
    }

    public DiaryDetailResponse displayDiary(User user, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateDisplayDiary(user, diary);
        return DiaryDetailResponse.from(diary, user.getId(),
                diaryCommentCountService.getDiaryCommentCountByDiaryId(diaryId));
    }

    private void validateDisplayDiary(User user, Diary diary) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), diary.getPet().getId()))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    public Slice<DiaryGroupByDateResponse> displayDiaries(User user, Long petId, int page, int size) {
        validateQueryDiaries(user, petId);
        return getGroupedDiariesSlice(
                diaryRepository.findByPetIdAndIsDeletedFalseOrderByIdDesc(petId,
                        PageRequest.of(page, size)), user.getId());
    }

    private Slice<DiaryGroupByDateResponse> getGroupedDiariesSlice(Slice<Diary> diarySlice, String userId) {
        if (diarySlice.getContent().isEmpty())
            return new SliceImpl<>(new ArrayList<>(), diarySlice.getPageable(), diarySlice.hasNext());

        List<DiaryGroupByDateResponse> content = new ArrayList<>();
        List<DiaryResponse> sameDaysDiaries = new ArrayList<>();
        LocalDate prevDate = diarySlice.getContent().get(0).getDate();
        for (Diary diary : diarySlice.getContent()) {
            if (!prevDate.equals(diary.getDate())) {
                content.add(DiaryGroupByDateResponse.of(prevDate, sameDaysDiaries));
                prevDate = diary.getDate();
                sameDaysDiaries = new ArrayList<>();
            }
            sameDaysDiaries.add(
                    DiaryResponse.from(diary, userId,
                            diaryCommentCountService.getDiaryCommentCountByDiaryId(diary.getId())));
        }
        content.add(DiaryGroupByDateResponse.of(prevDate, sameDaysDiaries));

        return new SliceImpl<>(content, diarySlice.getPageable(), diarySlice.hasNext());
    }

    private void validateQueryDiaries(User user, Long petId) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), petId))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }
}
