package com.ppp.domain.guardian.repository;

import com.ppp.domain.guardian.Guardian;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    @Cacheable(value = "petSpaceAuthority", key = "{#a0, #a1}")
    boolean existsByUserIdAndPetId(String userId, Long petId);
}
