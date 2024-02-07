package com.ppp.api.diary.service;

import com.ppp.domain.diary.DiaryDocument;
import com.ppp.domain.diary.repository.DiaryRepository;
import com.ppp.domain.diary.repository.DiarySearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiarySearchService {
    private final DiaryRepository diaryRepository;
    private final DiarySearchRepository diarySearchRepository;

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
}
