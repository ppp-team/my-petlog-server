package com.ppp.api.subscription.service;

import com.ppp.api.pet.exception.ErrorCode;
import com.ppp.api.pet.exception.PetException;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.subscription.Subscription;
import com.ppp.domain.subscription.repository.SubscriptionRepository;
import com.ppp.domain.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PetRepository petRepository;

    @Transactional
    public void subscribeOrUnsubscribe(Long petId, User user) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

        subscriptionRepository.findByUserAndPet(user, pet)
                .ifPresentOrElse(
                        subscriptionRepository::delete,
                        () -> {
                            subscriptionRepository.save(Subscription.builder()
                                    .subscriber(user)
                                    .pet(pet)
                                    .build());
                        });
    }
}
