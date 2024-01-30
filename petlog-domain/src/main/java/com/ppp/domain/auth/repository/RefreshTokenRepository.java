package com.ppp.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);
    boolean existsByEmail(String userEmail);
    void deleteByEmail(String userEmail);

}