package com.ppp.domain.pet.repository;

import com.ppp.domain.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByIdAndIsDeletedFalse(Long id);
}
