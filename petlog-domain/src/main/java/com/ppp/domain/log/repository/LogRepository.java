package com.ppp.domain.log.repository;

import com.ppp.domain.log.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    Optional<Log> findByIdAndIsDeletedFalse(Long id);
    List<Log> findByPetIdAndAndDatetimeBetweenAndIsDeletedFalse(Long petId, LocalDateTime start, LocalDateTime end);

    Slice<Log> findByPetIdAndAndDatetimeAfterAndIsDeletedFalse(Long petId, LocalDateTime start, PageRequest request);
}
