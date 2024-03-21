package com.ppp.api.subscription.service;

import com.ppp.api.pet.exception.ErrorCode;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.subscription.dto.response.SubscriberResponse;
import com.ppp.api.subscription.dto.response.SubscribingPetResponse;
import com.ppp.api.subscription.exception.SubscriptionException;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetQuerydslRepository;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.subscription.Subscription;
import com.ppp.domain.subscription.repository.SubscriptionRepository;
import com.ppp.domain.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ppp.api.subscription.exception.ErrorCode.FORBIDDEN_PET_SPACE;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PetRepository petRepository;
    private final PetQuerydslRepository petQuerydslRepository;

    @Transactional
    public void subscribeOrUnsubscribe(Long petId, User user) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));

        subscriptionRepository.findBySubscriberAndPet(user, pet)
                .ifPresentOrElse(
                        subscriptionRepository::delete,
                        () -> {
                            subscriptionRepository.save(Subscription.builder()
                                    .subscriber(user)
                                    .pet(pet)
                                    .build());
                        });
    }

    public List<SubscribingPetResponse> displayMySubscribingPets(User user) {
        return petQuerydslRepository.findSubscribingPetsByUserId(user.getId())
                .stream().map(SubscribingPetResponse::from)
                .collect(Collectors.toList());
    }

    public List<SubscriberResponse> displayMyPetsSubscribers(Long petId, User user) {
        validateManagePetsSubscribers(petId, user.getId());

        return subscriptionRepository.findByPetId(petId)
                .stream().map(SubscriberResponse::from)
                .collect(Collectors.toList());
    }

    private void validateManagePetsSubscribers(Long petId, String userId) {
        if (!petRepository.existsByIdAndUserIdAndIsDeletedFalse(petId, userId))
            throw new SubscriptionException(FORBIDDEN_PET_SPACE);
    }
}
