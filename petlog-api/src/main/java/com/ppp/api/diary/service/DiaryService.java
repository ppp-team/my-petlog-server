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
import com.ppp.api.notification.dto.event.DiaryNotificationEvent;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.video.exception.ErrorCode;
import com.ppp.api.video.exception.VideoException;
import com.ppp.common.service.FileStorageManageService;
import com.ppp.common.service.ThumbnailService;
import com.ppp.domain.common.constant.FileType;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryMedia;
import com.ppp.domain.diary.constant.DiaryMediaType;
import com.ppp.domain.diary.constant.DiaryPolicy;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.notification.constant.MessageCode;
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
    private final ThumbnailService thumbnailService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TempVideoRedisRepository tempVideoRedisRepository;

    @Transactional
    public void createDiary(User user, Long petId, DiaryCreateRequest request, List<MultipartFile> images) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        validateAccessDiary(petId, user);
        List<TempVideo> uploadedVideos = getUploadedVideos(request.getUploadedVideoIds(), user);

        Diary diary = Diary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(LocalDate.parse(request.getDate()))
                .user(user)
                .pet(pet)
                .build();
        diary.addDiaryMedias(uploadAndGetDiaryMedias(images, uploadedVideos, diary));

        applicationEventPublisher.publishEvent(
                new DiaryCreatedEvent(diaryRepository.save(diary).getId()));
    }

    private List<TempVideo> getUploadedVideos(List<String> videoIds, User user) {
        if (videoIds.isEmpty())
            return new ArrayList<>();
        return videoIds.stream().map(videoId ->
                tempVideoRedisRepository.findById(videoId).stream()
                        .filter(video -> Objects.equals(video.getUserId(), user.getId())).findFirst()
                        .orElseThrow(() -> new VideoException(ErrorCode.NOT_FOUND_VIDEO))).toList();
    }

    private List<DiaryMedia> uploadAndGetDiaryMedias(List<MultipartFile> images, List<TempVideo> tempVideos, Diary diary) {
        List<DiaryMedia> diaryMedias = uploadImagesIfNeeded(images, diary);
        uploadVideoIfNeeded(diaryMedias, tempVideos, diary);
        return diaryMedias;
    }

    private List<DiaryMedia> uploadImagesIfNeeded(List<MultipartFile> images, Diary diary) {
        if (images == null || images.isEmpty())
            return new ArrayList<>();
        return fileStorageManageService.uploadImages(images, DIARY).stream()
                .map(uploadedPath -> DiaryMedia.of(diary, uploadedPath, DiaryMediaType.IMAGE))
                .collect(Collectors.toList());
    }

    private void uploadVideoIfNeeded(List<DiaryMedia> diaryMedias, List<TempVideo> tempVideos, Diary diary) {
        if (tempVideos.isEmpty())
            return;
        fileStorageManageService.uploadVideos(tempVideos, DIARY)
                .forEach(uploadedPath -> diaryMedias.add(DiaryMedia.of(diary, uploadedPath, DiaryMediaType.VIDEO)));
    }

    @Transactional
    public void updateDiary(User user, Long petId, Long diaryId, DiaryUpdateRequest request, List<MultipartFile> images) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .filter(foundDiary -> Objects.equals(foundDiary.getPet().getId(), petId))
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user, petId);
        List<DiaryMedia> keepingVideos = diary.getKeepingVideos(request.getDeletedMediaIds());
        List<DiaryMedia> keepingImages = diary.getKeepingImages(request.getDeletedMediaIds());
        validateMediaSize(keepingVideos.size(), keepingImages.size(),
                request.getUploadedVideoIds().size(), images == null ? 0 : images.size());
        List<TempVideo> newlyUploadedVideos = getUploadedVideos(request.getUploadedVideoIds(), user);

        List<DiaryMedia> diaryMediasToBeDeleted = getDiaryMediasToBoDeleted(diary, keepingVideos, keepingImages);
        List<DiaryMedia> diaryMediasToBeUpdated = uploadAndGetDiaryMedias(images, newlyUploadedVideos, diary);
        keepOldDiaryMedia(diaryMediasToBeUpdated, keepingVideos, keepingImages);

        applicationEventPublisher.publishEvent(new DiaryUpdatedEvent(diaryId, diaryMediasToBeDeleted, diary.getThumbnailPath()));
        diary.update(request.getTitle(), request.getContent(), LocalDate.parse(request.getDate()), diaryMediasToBeUpdated);
    }

    private List<DiaryMedia> getDiaryMediasToBoDeleted(Diary diary, List<DiaryMedia> keepingVideos, List<DiaryMedia> keepingImages) {
        List<DiaryMedia> diaryMediasToBeDeleted = new ArrayList<>(diary.getDiaryMedias());
        diaryMediasToBeDeleted.removeAll(keepingImages);
        diaryMediasToBeDeleted.removeAll(keepingVideos);
        return diaryMediasToBeDeleted;
    }

    private void keepOldDiaryMedia(List<DiaryMedia> updatedMedias, List<DiaryMedia> keepingVideos, List<DiaryMedia> keepingImages) {
        updatedMedias.addAll(keepingImages);
        updatedMedias.addAll(keepingVideos);
    }

    private void validateMediaSize(int keepingVideoSize, int keepingImageSize, int requestedVideoSize, int requestedImageSize) {
        if (keepingVideoSize + requestedVideoSize > DiaryPolicy.VIDEO_UPLOAD_LIMIT
                || keepingImageSize + requestedImageSize > DiaryPolicy.IMAGE_UPLOAD_LIMIT)
            throw new DiaryException(MEDIA_UPLOAD_LIMIT_OVER);
    }

    private void validateModifyDiary(Diary diary, User user, Long petId) {
        if (!Objects.equals(diary.getUser().getId(), user.getId()))
            throw new DiaryException(NOT_DIARY_OWNER);
        validateAccessDiary(petId, user);
    }


    @Transactional
    public void deleteDiary(User user, Long petId, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .filter(foundDiary -> Objects.equals(foundDiary.getPet().getId(), petId))
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateModifyDiary(diary, user, petId);

        applicationEventPublisher.publishEvent(new DiaryDeletedEvent(diaryId,
                new ArrayList<>(diary.getDiaryMedias()), diary.getThumbnailPath()));
        diary.delete();
    }

    public DiaryDetailResponse displayDiary(User user, Long petId, Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .filter(foundDiary -> Objects.equals(foundDiary.getPet().getId(), petId))
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
                diaryRepository.findByPetIdAndIsDeletedFalseOrderByDateDesc(petId,
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

    @Transactional
    public void likeDiary(User user, Long petId, Long diaryId) {
        validateLikeDiary(user, petId, diaryId);

        if (diaryRedisService.isLikeExistByDiaryIdAndUserId(diaryId, user.getId()))
            diaryRedisService.cancelLikeByDiaryIdAndUserId(diaryId, user.getId());
        else {
            diaryRedisService.registerLikeByDiaryIdAndUserId(diaryId, user.getId());
            diaryRepository.findByIdAndIsDeletedFalse(diaryId).ifPresent(diary ->
                applicationEventPublisher.publishEvent(new DiaryNotificationEvent(MessageCode.DIARY_LIKE, user, diary))
            );
        }
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

    @Transactional
    public Diary saveThumbnail(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        List<DiaryMedia> diaryMedias = diary.getDiaryMedias();
        if (diaryMedias.isEmpty()) {
            diary.addThumbnail(DiaryPolicy.DEFAULT_THUMBNAIL_PATH);
        } else {
            diary.addThumbnail(getThumbnailFromDiaryMedia(diaryMedias.get(0)));
        }
        return diary;
    }

    public String getThumbnailFromDiaryMedia(DiaryMedia thumbnailMedia) {
        try {
            if (DiaryMediaType.IMAGE.equals(thumbnailMedia.getType()))
                return thumbnailService.uploadThumbnailFromStorageFile(thumbnailMedia.getPath(), FileType.IMAGE, DIARY);
            return thumbnailService.uploadThumbnailFromStorageFile(thumbnailMedia.getPath(), FileType.VIDEO, DIARY);
        } catch (Exception e) {
            return DiaryPolicy.DEFAULT_THUMBNAIL_PATH;
        }
    }
}
