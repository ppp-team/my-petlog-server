package com.ppp.domain.pet.repository;

import com.ppp.domain.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByIdAndIsDeletedFalse(Long id);

    List<Pet> findAllByUserId(String userId);

    @Query("SELECT p " +
            "FROM Pet p " +
            "WHERE p.id = :petId " +
            "AND p.user.id = :userId " +
            "AND p.isDeleted = false")
    Optional<Pet> findMyPetById(@Param("petId") Long petId, @Param("userId") String userId);

    @Query("SELECT p " +
            "FROM Pet p " +
            "WHERE p.user.id = :userId " +
            "AND p.isDeleted = false " +
            "AND p.repStatus = 'REPRESENTATIVE'")
    Optional<Pet> findRepresentativePet(@Param("userId") String userId);
}
