package com.ppp.domain.email.repository;


import com.ppp.domain.email.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);

    Optional<EmailVerification> findByEmailAndVerificationCode(String email, int verificationCode);
}