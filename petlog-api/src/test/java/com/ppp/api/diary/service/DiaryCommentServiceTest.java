package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.request.DiaryCommentRequest;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryComment;
import com.ppp.domain.diary.repository.DiaryCommentRepository;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ppp.api.diary.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiaryCommentServiceTest {
    @Mock
    private DiaryCommentRepository diaryCommentRepository;
    @Mock
    private DiaryRepository diaryRepository;
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DiaryCommentCountRedisService diaryCommentCountRedisService;
    @InjectMocks
    private DiaryCommentService diaryCommentService;

    User user = User.builder()
            .id("abcde1234")
            .nickname("hi")
            .build();

    User userA = User.builder()
            .id("abc123")
            .nickname("첫째누나")
            .build();

    Pet pet = Pet.builder()
            .id(1L).build();

    Diary diary = Diary.builder()
            .title("우리집 고양이")
            .content("츄르를 좋아해")
            .date(LocalDate.of(2020, 11, 11))
            .user(user)
            .pet(pet).build();

    Map<String, String> taggedUserIdNicknameMap = Map.of("ljf123", "둘째누나");

    @Test
    @DisplayName("다이어리 댓글 생성 성공")
    void createComment_success() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", List.of("abc123", "dab456"));

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(true);
        given(userRepository.findById("abc123")).willReturn(Optional.of(userA));
        given(userRepository.findById("dab456")).willReturn(Optional.empty());
        //when
        diaryCommentService.createComment(user, 1L, request);
        ArgumentCaptor<DiaryComment> diaryCommentArgumentCaptor = ArgumentCaptor.forClass(DiaryComment.class);
        //then
        verify(diaryCommentRepository, times(1)).save(diaryCommentArgumentCaptor.capture());
        assertEquals(diaryCommentArgumentCaptor.getValue().getDiary(), diary);
        assertEquals(diaryCommentArgumentCaptor.getValue().getUser(), user);
        assertEquals(diaryCommentArgumentCaptor.getValue().getContent(), "오늘은 산으로 산책을 갔어요");
        assertEquals(diaryCommentArgumentCaptor.getValue().getTaggedUsersIdNicknameMap().size(), 1);
        assertTrue(diaryCommentArgumentCaptor.getValue().getTaggedUsersIdNicknameMap().containsKey("abc123"));
        assertFalse(diaryCommentArgumentCaptor.getValue().getTaggedUsersIdNicknameMap().containsKey("dab456"));
        assertTrue(diaryCommentArgumentCaptor.getValue().getTaggedUsersIdNicknameMap().containsValue("첫째누나"));
    }

    @Test
    @DisplayName("다이어리 댓글 생성 성공-빈 태깅 유저 리스트가 주어졌을 때")
    void createComment_success_whenTaggedUserIdsEmpty() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", new ArrayList<>());

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(true);
        //when
        diaryCommentService.createComment(user, 1L, request);
        ArgumentCaptor<DiaryComment> diaryCommentArgumentCaptor = ArgumentCaptor.forClass(DiaryComment.class);
        //then
        verify(diaryCommentRepository, times(1)).save(diaryCommentArgumentCaptor.capture());
        assertEquals(diaryCommentArgumentCaptor.getValue().getDiary(), diary);
        assertEquals(diaryCommentArgumentCaptor.getValue().getUser(), user);
        assertEquals(diaryCommentArgumentCaptor.getValue().getContent(), "오늘은 산으로 산책을 갔어요");
        assertEquals(diaryCommentArgumentCaptor.getValue().getTaggedUsersIdNicknameMap().size(), 0);
    }

    @Test
    @DisplayName("다이어리 댓글 생성 실패-diary not found")
    void createComment_fail_DIARY_NOT_FOUND() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", List.of("abc123", "dab456"));

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryCommentService.createComment(user, 1L, request));
        //then
        assertEquals(DIARY_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("다이어리 댓글 생성 실패-forbidden pet space")
    void createComment_fail_FORBIDDEN_PET_SPACE() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", List.of("abc123", "dab456"));

        given(diaryRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diary));
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(false);
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryCommentService.createComment(user, 1L, request));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.name(), exception.getCode());
    }

    @Test
    @DisplayName("다이어리 댓글 수정 성공")
    void updateComment_success() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", List.of("abc123", "dab456"));

        DiaryComment diaryComment = DiaryComment.builder()
                .content("오늘은 바다로 산책을 갔어요")
                .taggedUsersIdNicknameMap(taggedUserIdNicknameMap)
                .diary(diary)
                .user(user)
                .build();
        given(diaryCommentRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diaryComment));
        given(userRepository.findById("abc123")).willReturn(Optional.of(userA));
        given(userRepository.findById("dab456")).willReturn(Optional.empty());
        //when
        diaryCommentService.updateComment(user, 1L, request);
        ArgumentCaptor<DiaryComment> diaryCommentArgumentCaptor = ArgumentCaptor.forClass(DiaryComment.class);
        //then
        assertEquals(diaryComment.getDiary(), diary);
        assertEquals(diaryComment.getUser(), user);
        assertEquals(diaryComment.getContent(), "오늘은 산으로 산책을 갔어요");
        assertEquals(diaryComment.getTaggedUsersIdNicknameMap().size(), 1);
        assertTrue(diaryComment.getTaggedUsersIdNicknameMap().containsKey("abc123"));
        assertFalse(diaryComment.getTaggedUsersIdNicknameMap().containsKey("dab456"));
        assertTrue(diaryComment.getTaggedUsersIdNicknameMap().containsValue("첫째누나"));
    }

    @Test
    @DisplayName("다이어리 댓글 수정 실패-diary comment not found")
    void updateComment_success_DIARY_COMMENT_NOT_FOUND() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", List.of("abc123", "dab456"));

        given(diaryCommentRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryCommentService.updateComment(user, 1L, request));
        //then
        assertEquals(DIARY_COMMENT_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("다이어리 댓글 수정 실패-not diary comment owner")
    void updateComment_fail_NOT_DIARY_COMMENT_OWNER() {
        //given
        DiaryCommentRequest request = new DiaryCommentRequest("오늘은 산으로 산책을 갔어요", List.of("abc123", "dab456"));

        User otherUser = User.builder()
                .id("other-user").build();
        DiaryComment diaryComment = DiaryComment.builder()
                .content("오늘은 바다로 산책을 갔어요")
                .taggedUsersIdNicknameMap(taggedUserIdNicknameMap)
                .diary(diary)
                .user(otherUser)
                .build();
        given(diaryCommentRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diaryComment));
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryCommentService.updateComment(user, 1L, request));
        //then
        assertEquals(NOT_DIARY_COMMENT_OWNER.name(), exception.getCode());
    }

    @Test
    @DisplayName("다이어리 댓글 삭제 성공")
    void deleteComment_success() {
        //given
        DiaryComment diaryComment = DiaryComment.builder()
                .content("오늘은 바다로 산책을 갔어요")
                .taggedUsersIdNicknameMap(taggedUserIdNicknameMap)
                .diary(diary)
                .user(user)
                .build();
        given(diaryCommentRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diaryComment));
        //when
        diaryCommentService.deleteComment(user, 1L);
        //then
        assertTrue(diaryComment.isDeleted());
    }

    @Test
    @DisplayName("다이어리 댓글 삭제 실패-comment not found")
    void deleteComment_success_DIARY_COMMENT_NOT_FOUND() {
        //given
        given(diaryCommentRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryCommentService.deleteComment(user, 1L));
        //then
        assertEquals(DIARY_COMMENT_NOT_FOUND.name(), exception.getCode());
    }

    @Test
    @DisplayName("다이어리 댓글 삭제 실패-diary comment not found")
    void deleteComment_fail_DIARY_COMMENT_NOT_FOUND() {
        //given
        User otherUser = User.builder()
                .id("other-user").build();
        DiaryComment diaryComment = DiaryComment.builder()
                .content("오늘은 바다로 산책을 갔어요")
                .taggedUsersIdNicknameMap(taggedUserIdNicknameMap)
                .diary(diary)
                .user(otherUser)
                .build();
        given(diaryCommentRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(diaryComment));
        //when
        DiaryException exception = assertThrows(DiaryException.class, () -> diaryCommentService.deleteComment(user, 1L));
        //then
        assertEquals(NOT_DIARY_COMMENT_OWNER.name(), exception.getCode());
    }

}