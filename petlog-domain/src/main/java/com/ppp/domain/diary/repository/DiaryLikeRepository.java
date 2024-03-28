package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.DiaryLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryLikeRepository extends JpaRepository<DiaryLike, Long> {
      boolean existsByDiaryIdAndUserId(Long diaryId, String userId);
}
