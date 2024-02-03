package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {
    Optional<DiaryComment> findByIdAndIsDeletedFalse(Long id);
    List<DiaryComment> findByDiaryAndIsDeletedFalse(Diary diary);
    boolean existsByIdAndIsDeletedFalse(Long id);
}
