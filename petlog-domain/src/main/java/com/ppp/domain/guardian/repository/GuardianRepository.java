package com.ppp.domain.guardian.repository;

import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.guardian.constant.RepStatus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    @Cacheable(value = "petSpaceAuthority", key = "{#a0, #a1}")
    boolean existsByUserIdAndPetId(String userId, Long petId);

    boolean existsByPetIdAndGuardianRole(Long petId, GuardianRole guardianRole);

    List<Guardian> findAllByPetIdOrderByCreatedAtDesc(Long petId);

    Optional<Guardian> findByUserIdAndPetId(String userId, Long petId);

    Optional<Guardian> findByUserIdAndRepStatus(String id, RepStatus repStatus);

    boolean existsByPetIdAndUserId(Long petId, String userId);
}
