package com.ppp.domain.pet.repository;

import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetImageRepository extends JpaRepository<PetImage, Long> {
    Optional<PetImage> findByPet(Pet pet);
}