package com.ppp.domain.log.repository;

import com.ppp.domain.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    Optional<Log> findByIdAndIsDeletedFalse(Long id);
}
