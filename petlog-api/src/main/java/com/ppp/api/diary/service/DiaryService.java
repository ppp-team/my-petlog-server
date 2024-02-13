package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.event.DiaryCreatedEvent;
import com.ppp.api.diary.dto.event.DiaryDeletedEvent;
import com.ppp.api.diary.dto.event.DiaryUpdatedEvent;
import com.ppp.api.diary.dto.request.DiaryCreateRequest;
import com.ppp.api.diary.dto.request.DiaryUpdateRequest;
import com.ppp.api.diary.dto.response.DiaryDetailResponse;
import com.ppp.api.diary.dto.response.DiaryGroupByDateResponse;
import com.ppp.api.diary.dto.response.DiaryResponse;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.video.exception.ErrorCode;
import com.ppp.api.video.exception.VideoException;
import com.ppp.common.service.FileStorageManageService;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryMedia;
import com.ppp.domain.diary.constant.DiaryMediaType;
import com.ppp.domain.diary.constant.DiaryPolicy;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.video.TempVideo;
import com.ppp.domain.video.repository.TempVideoRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final FileStorageManageService fileStorageManageService;
    private final DiaryCommentRedisService diaryCommentRedisService;
    private final DiaryRedisService diaryRedisService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TempVideoRedisRepository tempVideoRedisRepository;

    @Transactional
    public void createDiary(User user, Long petId, DiaryCreateRequest request, List<MultipartFile> images) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        validateAccessDiary(petId, user);

        Diary diary = Diary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(LocalDate.parse(request.getDate()))
                .user(user)
                .pet(pet)
                .build();
        diary.addDiaryMedias(uploadAndGetDiaryMedias(images, request.getUploadedVideoIds(), diary, user));
        applicationEventPublisher.publishEvent(
                new DiaryCreatedEvent(diaryRepository.save(diary).getId()));
    }

    private List<DiaryMedia> uploadAndGetDiaryMedias(List<MultipartFile> images, List<String> videoIds, Diary diary, User user) {
        List<DiaryMedia> diaryMedias = uploadImagesIfNeeded(images, diary);
        uploadVideoIfNeeded(diaryMedias, videoIds, diary, user);
        return diaryMedias;
    }

    private List<DiaryMedia> uploadImagesIfNeeded(List<MultipartFile> images, Diary diary) {
        if (images == null || images.isEmpty())
            return new ArrayList<>();
        return fileStorageManageService.uploadImages(images, DIARY).stream()
                .map(uploadedPath -> DiaryMedia.of(diary, uploadedPath, DiaryMediaType.IMAGE))
                .collect(Collectors.toList());
    }

    private void uploadVideoIfNeeded(List<DiaryMedia> diaryMedias, List<String> videoIds, Diary diary, User user) {
        if (videoIds == null || videoIds.isEmpty())
            return;
        List<TempVideo> tempVideos = videoIds.stream().map(videoId ->
                tempVideoRedisRepository.findById(videoId).stream()
                        .filter(video -> Objects.equals(video.getUserId(), user.getId())).findFirst()
                        .orElseThrow(() -> new VideoException(ErrorCode.NOT_FOUND_VIDEO))).toList();

        fileStorageManageService.uploadVideos(tempVideos, DIARY)
                .forEach(uploadedPath -> diaryMedias.add(DiaryMedia.of(diary, uploadedPath, DiaryMediaType.VIDEO)));
    }

    @Transactional
    public void updateDiary(User user, Long petId, Long diaryId, DiaryUpdateRequest request, List<MultipartFile> images) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user, petId);

        List<DiaryMedia> maintainedVideos = diary.getVideoMedias().stream()
                .filter(video -> !request.getDeletedVideoIds().contains(video.getId()))
                .toList();

        if (maintainedVideos.size() + request.getUploadedVideoIds().size() > DiaryPolicy.VIDEO_UPLOAD_LIMIT) {
            throw new DiaryException(MEDIA_UPLOAD_LIMIT_OVER);
        }

        List<DiaryMedia> diaryMediasToBeDeleted = new ArrayList<>(diary.getDiaryMedias());
        List<DiaryMedia> diaryMediasToBeUpdated = uploadAndGetDiaryMedias(images, request.getUploadedVideoIds(), diary, user);

        diaryMediasToBeDeleted.removeAll(maintainedVideos);
        diaryMediasToBeUpdated.addAll(maintainedVideos);
        diary.update(request.getTitle(), request.getContent(), LocalDate.parse(request.getDate()), diaryMediasToBeUpdated);
        applicationEventPublisher.publishEvent(new DiaryUpdatedEvent(diaryId, diaryMediasToBeDeleted));
    }

    private void validateModifyDiary(Diary diary, User user, Long petId) {
        if (!Objects.equals(diary.getUser().getId(), user.getId()))
            throw new DiaryException(NOT_DIARY_OWNER);
        validateAccessDiary(petId, user);
    }


    @Transactional
    public void deleteDiary(User user, Long petId, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user, petId);

        applicationEventPublisher.publishEvent(new DiaryDeletedEvent(diaryId, new ArrayList<>(diary.getDiaryMedias())));
        diary.delete();
    }

    public DiaryDetailResponse displayDiary(User user, Long petId, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateAccessDiary(petId, user);
        return DiaryDetailResponse.from(diary, user.getId(),
                diaryCommentRedisService.getDiaryCommentCountByDiaryId(diaryId),
                diaryRedisService.isLikeExistByDiaryIdAndUserId(diaryId, user.getId()),
                diaryRedisService.getLikeCountByDiaryId(diaryId));
    }

    public Slice<DiaryGroupByDateResponse> displayDiaries(User user, Long petId, int page, int size) {
        validateAccessDiary(petId, user);
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
                            diaryCommentRedisService.getDiaryCommentCountByDiaryId(diary.getId())));
        }
        content.add(DiaryGroupByDateResponse.of(prevDate, sameDaysDiaries));

        return new SliceImpl<>(content, diarySlice.getPageable(), diarySlice.hasNext());
    }

    public void likeDiary(User user, Long petId, Long diaryId) {
        validateLikeDiary(user, petId, diaryId);

        if (diaryRedisService.isLikeExistByDiaryIdAndUserId(diaryId, user.getId()))
            diaryRedisService.cancelLikeByDiaryIdAndUserId(diaryId, user.getId());
        else
            diaryRedisService.registerLikeByDiaryIdAndUserId(diaryId, user.getId());
    }

    private void validateLikeDiary(User user, Long petId, Long diaryId) {
        if (!diaryRepository.existsByIdAndIsDeletedFalse(diaryId))
            throw new DiaryException(DIARY_NOT_FOUND);
        validateAccessDiary(petId, user);
    }

    private void validateAccessDiary(Long petId, User user) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), petId))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }
}
