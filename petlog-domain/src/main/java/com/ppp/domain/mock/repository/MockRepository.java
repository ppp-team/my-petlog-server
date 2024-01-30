package com.ppp.domain.mock.repository;

import com.ppp.domain.mock.Mock;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MockRepository extends JpaRepository<Mock, Long> {
    @Cacheable(value = "mocks", unless = "#a0=='foundation@email.com'")
    Optional<Mock> findFirstByEmail(String email);
}
