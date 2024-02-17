package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryComment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {
    Optional<DiaryComment> findByIdAndIsDeletedFalse(Long id);

    Slice<DiaryComment> findByDiaryAndIsDeletedFalse(Diary diary, PageRequest request);

    boolean existsByIdAndIsDeletedFalse(Long id);

    @Query("select c from DiaryComment c left join Diary d on d.id = c.diary.id where c.id = :id and d.pet.id = :petId")
    Optional<DiaryComment> findByIdAndPetIdAndIsDeletedFalse(@Param("id") Long id, @Param("petId") Long petId);
}
