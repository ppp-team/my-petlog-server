package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.Diary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByIdAndIsDeletedFalse(Long id);
    Slice<Diary> findByPetIdAndIsDeletedFalseOrderByIdDesc(Long petId, PageRequest pageRequest);
}
