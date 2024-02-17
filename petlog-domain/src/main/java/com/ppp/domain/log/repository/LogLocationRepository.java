package com.ppp.domain.log.repository;

import com.ppp.domain.log.Log;
import com.ppp.domain.log.LogLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogLocationRepository extends JpaRepository<LogLocation, Long> {
    Optional<LogLocation> findByLog(Log log);
}
