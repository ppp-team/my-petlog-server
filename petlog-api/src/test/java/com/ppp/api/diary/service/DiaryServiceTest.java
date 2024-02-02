package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.dto.response.DiaryDetailResponse;
import com.ppp.api.diary.dto.response.DiaryGroupByDateResponse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.ppp.api.diary.exception.ErrorCode.*;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {
    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private FileManageService fileManageService;
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private DiaryCommentCountRedisService diaryCommentCountRedisService;

    @InjectMocks
    private DiaryService diaryService;

    User user = User.builder()
            .id("randomstring")
            .nickname("hi")
            .build();

    Pet pet = Pet.builder()
            .id(1L).build();

    List<MultipartFile> images = List.of(
            new MockMultipartFile("images", "image.jpg",
                    MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()),
            new MockMultipartFile("images", "image.jpg",
                    MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes())
    );

    @Test
    @DisplayName("일기 생성 성공")
    void createDiary_success() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(fileManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("/DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "/DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));
        //when
        diaryService.createDiary(user, 1L, request, images);
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        //then
        verify(diaryRepository, times(1)).save(diaryCaptor.capture());
        assertEquals(request.getTitle(), diaryCaptor.getValue().getTitle());
        assertEquals(request.getContent(), diaryCaptor.getValue().getContent());
        assertEquals(request.getDate(), diaryCaptor.getValue().getDate());
        assertEquals(user.getId(), diaryCaptor.getValue().getUser().getId());
        assertEquals(pet.getId(), diaryCaptor.getValue().getPet().getId());
        assertEquals(2, diaryCaptor.getValue().getDiaryMedias().size());
    }

    @Test
    @DisplayName("일기 생성 실패-pet not found")
    void createDiary_fail_PET_NOT_FOUND() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());

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
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());

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
    @DisplayName("일기 수정 성공")
    void updateDiary_success() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());
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
        given(fileManageService.uploadImages(anyList(), any()))
                .willReturn(List.of("/DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg",
                        "/DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));

        //when
        diaryService.updateDiary(user, 1L, 1L, request, images);
        //then
        assertEquals(request.getTitle(), diary.getTitle());
        assertEquals(request.getContent(), diary.getContent());
        assertEquals(request.getDate(), diary.getDate());
        assertEquals(2, diary.getDiaryMedias().size());
    }

    @Test
    @DisplayName("일기 수정 실패-diary not found")
    void updateDiary_fail_DIARY_NOT_FOUND() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, 1L, request, images));
        //then
        assertEquals(DIARY_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-not diary owner")
    void updateDiary_fail_NOT_DIARY_OWNER() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());
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
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now());
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
    @DisplayName("일기 수정 실패-diary not found")
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
    @DisplayName("일기 수정 실패-not diary owner")
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
    @DisplayName("일기 조회 성공")
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
                        .path("/DIARY/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg")
                        .build()));
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
                .willReturn(true);
        given(diaryCommentCountRedisService.getDiaryCommentCountByDiaryId(anyLong()))
                .willReturn(3);
        //when
        DiaryDetailResponse response = diaryService.displayDiary(user, 1L, 1L);
        //then
        assertEquals(response.title(), diary.getTitle());
        assertEquals(response.content(), diary.getContent());
        assertEquals(response.images().size(), 1);
        assertEquals(response.commentCount(), 3);
        assertEquals(response.writer().nickname(), user.getNickname());
    }

    @Test
    @DisplayName("일기 조회 실패-diary not found")
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
    @DisplayName("일기 조회 실패-forbidden pet space")
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
    @DisplayName("일기 조회 성공")
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
        given(diaryCommentCountRedisService.getDiaryCommentCountByDiaryId(any()))
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