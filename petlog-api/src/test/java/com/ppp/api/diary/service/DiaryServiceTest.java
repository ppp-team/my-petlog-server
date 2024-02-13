package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.event.DiaryUpdatedEvent;
import com.ppp.api.diary.dto.request.DiaryCreateRequest;
import com.ppp.api.diary.dto.request.DiaryUpdateRequest;
import com.ppp.api.diary.dto.response.DiaryDetailResponse;
import com.ppp.api.diary.dto.response.DiaryGroupByDateResponse;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.video.exception.VideoException;
import com.ppp.common.service.FileStorageManageService;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryMedia;
import com.ppp.domain.diary.constant.DiaryMediaType;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.video.TempVideo;
import com.ppp.domain.video.repository.TempVideoRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ppp.api.diary.exception.ErrorCode.*;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;
import static com.ppp.api.video.exception.ErrorCode.NOT_FOUND_VIDEO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {
    @Mock
    private DiaryRepository diaryRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private FileStorageManageService fileStorageManageService;
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private TempVideoRedisRepository tempVideoRedisRepository;
    @Mock
    private DiaryCommentRedisService diaryCommentRedisService;
    @Mock
    private DiaryRedisService diaryRedisService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    private DiaryService diaryService;

    User user = User.builder()
            .id("randomstring")
            .nickname("hi")
            .build();

    Pet pet = Pet.builder()
            .id(1L)
            .birth(LocalDateTime.of(2023, 2, 8, 0, 0))
            .build();

    List<MultipartFile> images = List.of(
            new MockMultipartFile("images", "image.jpg",
                    MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes()),
            new MockMultipartFile("images", "image.jpg",
                    MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes())
    );

    @Test
    @DisplayName("일기 생성 성공")
    void createDiary_success() {
        //given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .date(LocalDate.now().toString())
                .build();

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));
        Diary createdDiary = mock(Diary.class);
        given(diaryRepository.save(any())).willReturn(createdDiary);
        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.of(TempVideo.builder()
                        .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4")
                        .userId(user.getId())
                        .build()));
        given(fileStorageManageService.uploadVideo(any(), any()))
                .willReturn(Optional.of("VIDEO/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.mp4"));
        given(createdDiary.getId()).willReturn(1L);
        //when
        diaryService.createDiary(user, 1L, request, images);
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        //then
        verify(diaryRepository, times(1)).save(diaryCaptor.capture());
        assertEquals(request.getTitle(), diaryCaptor.getValue().getTitle());
        assertEquals(request.getContent(), diaryCaptor.getValue().getContent());
        assertEquals(request.getDate(), diaryCaptor.getValue().getDate().toString());
        assertEquals(user.getId(), diaryCaptor.getValue().getUser().getId());
        assertEquals(pet.getId(), diaryCaptor.getValue().getPet().getId());
        assertEquals(3, diaryCaptor.getValue().getDiaryMedias().size());
    }

    @Test
    @DisplayName("일기 생성 성공-비디오 아이디가 주어지지 않음")
    void createDiary_success_WhenVideoIdIsNotGiven() {
        //given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .build();

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));
        Diary createdDiary = mock(Diary.class);
        given(diaryRepository.save(any())).willReturn(createdDiary);
        //when
        diaryService.createDiary(user, 1L, request, images);
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        //then
        verify(diaryRepository, times(1)).save(diaryCaptor.capture());
        assertEquals(request.getTitle(), diaryCaptor.getValue().getTitle());
        assertEquals(request.getContent(), diaryCaptor.getValue().getContent());
        assertEquals(request.getDate(), diaryCaptor.getValue().getDate().toString());
        assertEquals(user.getId(), diaryCaptor.getValue().getUser().getId());
        assertEquals(pet.getId(), diaryCaptor.getValue().getPet().getId());
        assertEquals(2, diaryCaptor.getValue().getDiaryMedias().size());
    }

    @Test
    @DisplayName("일기 생성 실패-pet not found")
    void createDiary_fail_PET_NOT_FOUND() {
        //given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        PetException exception = assertThrows(PetException.class, () -> diaryService.createDiary(user, 1L, request, images));
        //then
        assertEquals(PET_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 생성 실패-forbidden pet space")
    void createDiary_fail_FORBIDDEN_PET_SPACE() {
        //given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(false);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.createDiary(user, 1L, request, images));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 생성 실패-not found video")
    void createDiary_fail_NOT_FOUND_VIDEO() {
        //given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .date(LocalDate.now().toString())
                .build();

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));

        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.empty());
        //when
        VideoException exception = assertThrows(VideoException.class, () -> diaryService.createDiary(user, 1L, request, images));
        //then
        assertEquals(NOT_FOUND_VIDEO.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 생성 실패-not found video-비디오 유저 아이디가 다름")
    void createDiary_fail_NOT_FOUND_VIDEO_WhenUserIdNotMatched() {
        //given
        DiaryCreateRequest request = DiaryCreateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .date(LocalDate.now().toString())
                .build();

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));

        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.of(TempVideo.builder()
                        .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4")
                        .userId("123456")
                        .build()));
        //when
        VideoException exception = assertThrows(VideoException.class, () -> diaryService.createDiary(user, 1L, request, images));
        //then
        assertEquals(NOT_FOUND_VIDEO.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 성공-비디오가 삭제되지 않을때")
    void updateDiary_success_WhenIsVideoDeletedFalse() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .isVideoDeleted(false)
                .build();

        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();
        diary.addDiaryMedias(List.of(
                DiaryMedia.builder()
                        .type(DiaryMediaType.IMAGE)
                        .build(),
                DiaryMedia.builder()
                        .type(DiaryMediaType.VIDEO)
                        .build()));

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));
        //when
        diaryService.updateDiary(user, 1L, 1L, request, images);
        ArgumentCaptor<DiaryUpdatedEvent> captor = ArgumentCaptor.forClass(DiaryUpdatedEvent.class);
        //then
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        assertEquals(1, captor.getValue().getDiaryMedias().size());
        assertEquals(request.getTitle(), diary.getTitle());
        assertEquals(request.getContent(), diary.getContent());
        assertEquals(request.getDate(), diary.getDate().toString());
        assertEquals(3, diary.getDiaryMedias().size());
    }

    @Test
    @DisplayName("일기 수정 성공-처음 비디오를 업로드")
    void updateDiary_success_WhenVideoUploadedFirstTime() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .isVideoDeleted(false)
                .build();

        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();
        diary.addDiaryMedias(List.of(
                DiaryMedia.builder()
                        .type(DiaryMediaType.IMAGE)
                        .build()));

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));
        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.of(TempVideo.builder()
                        .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4")
                        .userId(user.getId())
                        .build()));
        given(fileStorageManageService.uploadVideo(any(), any()))
                .willReturn(Optional.of("VIDEO/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.mp4"));
        //when
        diaryService.updateDiary(user, 1L, 1L, request, images);
        ArgumentCaptor<DiaryUpdatedEvent> captor = ArgumentCaptor.forClass(DiaryUpdatedEvent.class);
        //then
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        assertEquals(1, captor.getValue().getDiaryMedias().size());
        assertEquals(request.getTitle(), diary.getTitle());
        assertEquals(request.getContent(), diary.getContent());
        assertEquals(request.getDate(), diary.getDate().toString());
        assertEquals(3, diary.getDiaryMedias().size());
    }

    @Test
    @DisplayName("일기 수정 성공-기존 비디오를 삭제하고 비디오를 업로드")
    void updateDiary_success_WhenVideoIdIsNotGiven() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .isVideoDeleted(true)
                .build();

        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();
        diary.addDiaryMedias(List.of(
                DiaryMedia.builder()
                        .type(DiaryMediaType.IMAGE)
                        .build(),
                DiaryMedia.builder()
                        .type(DiaryMediaType.VIDEO)
                        .build()));


        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileStorageManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));
        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.of(TempVideo.builder()
                        .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4")
                        .userId(user.getId())
                        .build()));
        given(fileStorageManageService.uploadVideo(any(), any()))
                .willReturn(Optional.of("VIDEO/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.mp4"));

        //when
        diaryService.updateDiary(user, 1L, 1L, request, images);
        ArgumentCaptor<DiaryUpdatedEvent> captor = ArgumentCaptor.forClass(DiaryUpdatedEvent.class);
        //then
        verify(applicationEventPublisher, times(1)).publishEvent(captor.capture());
        assertEquals(2, captor.getValue().getDiaryMedias().size());
        assertEquals(3, diary.getDiaryMedias().size());
        assertEquals(request.getTitle(), diary.getTitle());
        assertEquals(request.getContent(), diary.getContent());
        assertEquals(request.getDate(), diary.getDate().toString());
    }


    @Test
    @DisplayName("일기 수정 실패-diary not found")
    void updateDiary_fail_DIARY_NOT_FOUND() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .isVideoDeleted(false)
                .build();
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(DIARY_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-media upload limit over")
    void updateDiary_fail_MEDIA_UPLOAD_LIMIT_OVER() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .isVideoDeleted(false)
                .build();

        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        diary.addDiaryMedias(List.of(
                DiaryMedia.builder()
                        .type(DiaryMediaType.IMAGE)
                        .build(),
                DiaryMedia.builder()
                        .type(DiaryMediaType.VIDEO)
                        .build()));

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(MEDIA_UPLOAD_LIMIT_OVER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-not diary owner")
    void updateDiary_fail_NOT_DIARY_OWNER() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .isVideoDeleted(false)
                .build();
        User otherUser = User.builder()
                .id("other-user").build();
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(otherUser)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(NOT_DIARY_OWNER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-forbidden pet space")
    void updateDiary_fail_FORBIDDEN_PET_SPACE() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .isVideoDeleted(false)
                .build();
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(false);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-not found video")
    void updateDiary_fail_NOT_FOUND_VIDEO() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .isVideoDeleted(false)
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .build();
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.empty());
        //when
        VideoException exception = assertThrows(VideoException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(NOT_FOUND_VIDEO.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-not found video-유저 아이디가 다름")
    void updateDiary_fail_NOT_FOUND_VIDEO_WhenUserIdNotMatched() {
        //given
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .title("우리 강아지")
                .content("너무 귀엽당")
                .date(LocalDate.now().toString())
                .isVideoDeleted(false)
                .videoId("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .build();
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(tempVideoRedisRepository.findById(anyString()))
                .willReturn(Optional.of(TempVideo.builder()
                        .id("c8e8f796-8e29-4067-86c4-0eae419a054e")
                        .userId("123456")
                        .build()));
        //when
        VideoException exception = assertThrows(VideoException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(NOT_FOUND_VIDEO.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 삭제 성공")
    void deleteDiary_success() {
        //given
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        //when
        diaryService.deleteDiary(user, 1L, 1L);
        //then
        assertTrue(diary.isDeleted());
    }

    @Test
    @DisplayName("일기 삭제 실패-diary not found")
    void deleteDiary_fail_DIARY_NOT_FOUND() {
        //given
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.deleteDiary(user, 1L, 1L));
        //then
        assertEquals(DIARY_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 삭제 실패-not diary owner")
    void deleteDiary_fail_NOT_DIARY_OWNER() {
        //given
        User otherUser = User.builder()
                .id("other-user").build();
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(otherUser)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.deleteDiary(user, 1L, 1L));
        //then
        assertEquals(NOT_DIARY_OWNER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 삭제 실패-forbidden pet space")
    void deleteDiary_fail_FORBIDDEN_PET_SPACE() {
        //given
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(false);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.deleteDiary(user, 1L, 1L));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 상세 조회 성공")
    void displayDiary_success() {
        //given
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();
        diary.addDiaryMedias(List.of(
                DiaryMedia.builder()
                        .type(DiaryMediaType.IMAGE)
                        .path("DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg")
                        .build()));
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(diaryCommentRedisService.getDiaryCommentCountByDiaryId(anyLong()))
                .willReturn(3);
        given(diaryRedisService.isLikeExistByDiaryIdAndUserId(anyLong(), anyString()))
                .willReturn(false);
        given(diaryRedisService.getLikeCountByDiaryId(anyLong())).willReturn(5);
        //when
        DiaryDetailResponse response = diaryService.displayDiary(user, 1L, 1L);
        //then
        assertEquals(response.title(), diary.getTitle());
        assertEquals(response.content(), diary.getContent());
        assertEquals(response.images().size(), 1);
        assertEquals(response.commentCount(), 3);
        assertEquals(response.writer().nickname(), user.getNickname());
        assertEquals(response.pet().age(), "1살");
        assertFalse(response.isCurrentUserLiked());
        assertEquals(response.likeCount(), 5);
    }

    @Test
    @DisplayName("일기 상세 조회 실패-diary not found")
    void displayDiary_fail_DIARY_NOT_FOUND() {
        //given
        User otherUser = User.builder()
                .id("other-user").build();
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.displayDiary(otherUser, 1L, 1L));
        //then
        assertEquals(DIARY_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 상세 조회 실패-forbidden pet space")
    void displayDiary_fail_FORBIDDEN_PET_SPACE() {
        //given
        User otherUser = User.builder()
                .id("other-user").build();
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(false);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.displayDiary(otherUser, 1L, 1L));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 리스트 조회 성공")
    void displayDiaries_success() {
        //given
        given(diaryRepository.findByPetIdAndIsDeletedFalseOrderByIdDesc(anyLong(), any()))
                .willReturn(new SliceImpl<>(List.of(
                        Diary.builder()
                                .title("우리집 고양이")
                                .content("츄르를 좋아해")
                                .date(LocalDate.of(2022, 12, 11))
                                .user(user)
                                .pet(pet).build(),
                        Diary.builder()
                                .title("우리집 고양이")
                                .content("츄르를 먹어")
                                .date(LocalDate.of(2022, 12, 11))
                                .user(user)
                                .pet(pet).build(),
                        Diary.builder()
                                .title("우리집 강아지")
                                .content("츄르를 싫어해")
                                .date(LocalDate.of(2020, 11, 11))
                                .user(user)
                                .pet(pet).build()
                )));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(diaryCommentRedisService.getDiaryCommentCountByDiaryId(any()))
                .willReturn(3);
        //when
        Slice<DiaryGroupByDateResponse> response = diaryService.displayDiaries(user, 1L, 10, 10);
        //then
        assertEquals(response.getContent().get(0).date(), LocalDate.of(2022, 12, 11));
        assertEquals(response.getContent().get(0).diaries().size(), 2);
        assertEquals(response.getContent().get(0).diaries().get(0).title(), "우리집 고양이");
        assertEquals(response.getContent().get(0).diaries().get(0).content(), "츄르를 좋아해");
        assertEquals(response.getContent().get(0).diaries().get(0).commentCount(), 3);
        assertEquals(response.getContent().get(1).diaries().get(0).title(), "우리집 강아지");
        assertEquals(response.getContent().get(1).diaries().get(0).content(), "츄르를 싫어해");
        assertEquals(response.getContent().get(1).diaries().get(0).commentCount(), 3);
    }

    @Test
    @DisplayName("일기 조회 실패-forbidden pet space")
    void displayDiaries_fail_FORBIDDEN_PET_SPACE() {
        //given
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(false);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.displayDiaries(user, 1L, 10, 10));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

}