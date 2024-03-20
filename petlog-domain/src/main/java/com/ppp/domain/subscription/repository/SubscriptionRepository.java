package com.ppp.domain.subscription.repository;

import com.ppp.domain.pet.Pet;
import com.ppp.domain.subscription.Subscription;
import com.ppp.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndPet(User user, Pet pet);
    @EntityGraph(attributePaths = {"subscriber"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Subscription> findByPetId(Long petId);
}
