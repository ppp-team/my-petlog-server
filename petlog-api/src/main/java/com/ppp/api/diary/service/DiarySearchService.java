package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.response.DiaryGroupByDateResponse;
import com.ppp.api.diary.dto.response.DiaryResponse;
import com.ppp.api.diary.exception.DiaryException;
import com.ppp.domain.diary.DiaryDocument;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.diary.repository.DiarySearchRepository;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.user.User;
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

import static com.ppp.api.diary.exception.ErrorCode.FORBIDDEN_PET_SPACE;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiarySearchService {
    private final DiaryRepository diaryRepository;
    private final DiarySearchRepository diarySearchRepository;
    private final DiaryCommentRedisService diaryCommentRedisService;
    private final GuardianRepository guardianRepository;

    public void save(Long diaryId) {
        diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .ifPresent(diary -> diarySearchRepository.save(DiaryDocument.from(diary)));
    }

    public void update(Long diaryId) {
        diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .ifPresent(diary -> diarySearchRepository.save(DiaryDocument.from(diary)));
    }

    public void delete(Long diaryId) {
        diarySearchRepository.deleteById(diaryId + "");
    }

    public Page<DiaryGroupByDateResponse> search(User user, String keyword, Long petId, int page, int size) {
        validateQueryDiaries(user, petId);
        return getGroupedDiariesPage(diarySearchRepository.
                findByTitleContainsOrContentContainsAndPetIdOrderByDateDesc(keyword, petId,
                        PageRequest.of(page, size, Sort.by("date").descending())), user.getId());
    }

    private void validateQueryDiaries(User user, Long petId) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), petId))
            throw new DiaryException(FORBIDDEN_PET_SPACE);
    }

    private Page<DiaryGroupByDateResponse> getGroupedDiariesPage(Page<DiaryDocument> documentPage, String userId) {
        if (documentPage.getContent().isEmpty())
            return new PageImpl<>(new ArrayList<>(), documentPage.getPageable(), documentPage.getTotalPages());

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
        return new PageImpl<>(content, documentPage.getPageable(), documentPage.getTotalPages());
    }
}