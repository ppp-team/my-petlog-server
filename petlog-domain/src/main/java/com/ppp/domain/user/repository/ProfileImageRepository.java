package com.ppp.domain.user.repository;

import com.ppp.domain.user.ProfileImage;
import com.ppp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByUser(User user);
}