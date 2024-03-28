package com.ppp.api.subscription.dto.transfer;

import com.ppp.domain.subscription.Subscription;
import lombok.Builder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record SubscriptionInfoDto(
        Set<Long> subscribedPetIds,
        Set<Long> blockedPetIds
) {
    public static SubscriptionInfoDto of(List<Subscription> subscribedPets, List<Subscription> blockedPets) {
        return SubscriptionInfoDto.builder()
                .subscribedPetIds(
                        subscribedPets.stream().map(subscription -> subscription.getPet().getId())
                                .collect(Collectors.toSet()))
                .blockedPetIds(
                        blockedPets.stream().map(subscription -> subscription.getPet().getId())
                                .collect(Collectors.toSet()))
                .build();
    }
}
