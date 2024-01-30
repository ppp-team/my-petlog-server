package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.DiaryMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryMediaRepository extends JpaRepository<DiaryMedia, Long> {

}
