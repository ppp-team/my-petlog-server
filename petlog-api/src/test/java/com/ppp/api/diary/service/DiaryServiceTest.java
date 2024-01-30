package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.repository.DiaryRepository;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.ppp.api.diary.exception.ErrorCode.DIARY_NOT_FOUND;
import static com.ppp.api.diary.exception.ErrorCode.NOT_DIARY_OWNER;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {
    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private DiaryService diaryService;

    User user = User.builder()
            .id("randomstring").build();

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
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), images);

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        //when
        diaryService.createDiary(user, 1L, request);
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        //then
        verify(diaryRepository, times(1)).save(diaryCaptor.capture());
        assertEquals(request.getTitle(), diaryCaptor.getValue().getTitle());
        assertEquals(request.getContent(), diaryCaptor.getValue().getContent());
        assertEquals(request.getDate(), diaryCaptor.getValue().getDate());
        assertEquals(user.getId(), diaryCaptor.getValue().getUser().getId());
        assertEquals(pet.getId(), diaryCaptor.getValue().getPet().getId());
    }

    @Test
    @DisplayName("일기 생성 실패-pet not found")
    void createDiary_fail_PET_NOT_FOUND() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), images);

        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        PetException exception = assertThrows(PetException.class, () -> diaryService.createDiary(user, 1L, request));
        //then
        assertEquals(PET_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 성공")
    void updateDiary_success() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), images);
        Diary diary = Diary.builder()
                .title("우리집 고양이")
                .content("츄르를 좋아해")
                .date(LocalDate.of(2020, 11, 11))
                .user(user)
                .pet(pet).build();

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        //when
        diaryService.updateDiary(user, 1L, request);
        //then
        assertEquals(request.getTitle(), diary.getTitle());
        assertEquals(request.getContent(), diary.getContent());
        assertEquals(request.getDate(), diary.getDate());
    }

    @Test
    @DisplayName("일기 수정 실패-diary not found")
    void updateDiary_fail_DIARY_NOT_FOUND() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), images);
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, request));
        //then
        assertEquals(DIARY_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("일기 수정 실패-not diary owner")
    void updateDiary_fail_NOT_DIARY_OWNER() {
        //given
        DiaryRequest request =
                new DiaryRequest("우리 강아지", "너무 귀엽당", LocalDate.now(), images);
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
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.updateDiary(user, 1L, request));
        //then
        assertEquals(NOT_DIARY_OWNER.name(), exception.getCode());
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
        //when
        diaryService.deleteDiary(user, 1L);
        //then
        assertEquals(diary.getIsDeleted(), true);
    }

    @Test
    @DisplayName("일기 수정 실패-diary not found")
    void deleteDiary_fail_DIARY_NOT_FOUND() {
        //given
        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.deleteDiary(user, 1L));
        //then
        assertEquals(DIARY_NOT_FOUND.name(), exception.getCode());
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
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryService.deleteDiary(user, 1L));
        //then
        assertEquals(NOT_DIARY_OWNER.name(), exception.getCode());
    }

}