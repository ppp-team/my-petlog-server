package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.response.DiaryGroupByDateResponse;
import com.ppp.api.diary.dto.response.DiaryResponse;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.UserException;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryDocument;
import com.ppp.domain.diary.repository.DiarySearchRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ppp.api.diary.exception.ErrorCode.FORBIDDEN_PET_SPACE;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiarySearchService {
    private final DiarySearchRepository diarySearchRepository;
    private final DiaryCommentRedisService diaryCommentRedisService;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;


    public void save(Diary diary) {
        diarySearchRepository.save(DiaryDocument.from(diary));
    }

    public void update(Diary diary) {
        diarySearchRepository.save(DiaryDocument.from(diary));
    }

    public void delete(Long diaryId) {
        diarySearchRepository.deleteById(diaryId + "");
    }

    public void updateUser(String userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        diarySearchRepository.saveAll(diarySearchRepository.findByUser_Id(userId)
                .stream().peek(diaryDocument -> diaryDocument.updateUser(user))
                .collect(Collectors.toList()));
    }

    public Page<DiaryGroupByDateResponse> search(User user, String keyword, Long petId, int page, int size) {
        validateQueryDiaries(user, petId);
        return getGroupedDiariesPage(diarySearchRepository.
                findByTitleContainsOrContentContainsAndPetIdOrderByDateDesc(keyword, petId,
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("date")))), user.getId());
    }

    private void validateQueryDiaries(User user, Long petId) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), petId))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    private Page<DiaryGroupByDateResponse> getGroupedDiariesPage(Page<DiaryDocument> documentPage, String userId) {
        if (documentPage.getContent().isEmpty())
            return new PageImpl<>(new ArrayList<>(), documentPage.getPageable(), documentPage.getTotalElements());
        List<DiaryGroupByDateResponse> content = new ArrayList<>();
        List<DiaryResponse> sameDaysDiaries = new ArrayList<>();
        LocalDate prevDate = LocalDate.ofEpochDay(documentPage.getContent().get(0).getDate());
        for (DiaryDocument document : documentPage.getContent()) {
            LocalDate currentDate = LocalDate.ofEpochDay(document.getDate());
            if (!prevDate.equals(currentDate)) {
                content.add(DiaryGroupByDateResponse.of(prevDate, sameDaysDiaries));
                prevDate = currentDate;
                sameDaysDiaries = new ArrayList<>();
            }
            sameDaysDiaries.add(
                    DiaryResponse.from(document, userId,
                            diaryCommentRedisService.getDiaryCommentCountByDiaryId(Long.parseLong(document.getId()))));
        }
        content.add(DiaryGroupByDateResponse.of(prevDate, sameDaysDiaries));
        return new PageImpl<>(content, documentPage.getPageable(), documentPage.getTotalElements());
    }
}
