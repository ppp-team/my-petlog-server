package com.ppp.domain.pet.repository;

import com.ppp.domain.pet.dto.PetDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ppp.domain.pet.QPet.pet;
import static com.ppp.domain.pet.QPetImage.petImage;
import static com.ppp.domain.subscription.QSubscription.subscription;
import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class PetQuerydslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<PetDto> findSubscribedPetsByUserId(String userId) {
        return jpaQueryFactory.select(constructor(PetDto.class, pet.id, petImage.url, pet.name))
                .from(subscription)
                .innerJoin(subscription.pet, pet)
                .leftJoin(petImage).on(petImage.pet.eq(pet))
                .where(subscriberIdEq(userId))
                .fetch();
    }

    private BooleanExpression subscriberIdEq(String userId) {
        return userId != null ? subscription.subscriber.id.eq(userId) : null;
    }
}
