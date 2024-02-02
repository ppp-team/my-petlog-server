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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ppp.api.diary.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class DiaryCommentService {

    private final DiaryCommentRepository diaryCommentRepository;
    private final DiaryRepository diaryRepository;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;
    private final DiaryCommentCountRedisService diaryCommentCountService;

    public void createComment(User user, Long diaryId, DiaryCommentRequest request) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));
        validateCreateComment(diary.getPet(), user);

        diaryCommentRepository.save(DiaryComment.builder()
                .content(request.getContent())
                .taggedUsersIdNicknameMap(getTaggedUsersIdNicknameMap(request))
                .diary(diary)
                .user(user)
                .build());
        diaryCommentCountService.increaseDiaryCommentCountByDiaryId(diaryId);
    }

    private Map<String, String> getTaggedUsersIdNicknameMap(DiaryCommentRequest request) {
        Map<String, String> taggedUsersIdNicknameMap = new HashMap<>();
        for (String id : request.getTaggedUserIds()) {
            userRepository.findById(id)
                    .ifPresent(taggedUser ->
                            taggedUsersIdNicknameMap.put(id, taggedUser.getNickname()));
        }
        return taggedUsersIdNicknameMap;
    }

    private void validateCreateComment(Pet pet, User user) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), pet.getId()))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    @Transactional
    public void updateComment(User user, Long commentId, DiaryCommentRequest request) {
        DiaryComment comment = diaryCommentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new DiaryException(DIARY_COMMENT_NOT_FOUND));
        validateModifyComment(comment, user);
        comment.update(request.getContent(), getTaggedUsersIdNicknameMap(request));
    }

    private void validateModifyComment(DiaryComment comment, User user) {
        if (!Objects.equals(comment.getUser().getId(), user.getId()))
            throw new DiaryException(NOT_DIARY_COMMENT_OWNER);
    }

    @Transactional
    public void deleteComment(User user, Long commentId) {
        DiaryComment comment = diaryCommentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new DiaryException(DIARY_COMMENT_NOT_FOUND));
        validateModifyComment(comment, user);
        comment.delete();
        diaryCommentCountService.decreaseDiaryCommentCountByDiaryId(comment.getDiary().getId());
    }

}
