package com.ppp.domain.guardian.repository;

import com.ppp.domain.guardian.dto.MyPetDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ppp.domain.guardian.QGuardian.guardian;
import static com.ppp.domain.pet.QPet.pet;
import static com.ppp.domain.pet.QPetImage.petImage;

@RequiredArgsConstructor
@Repository
public class GuardianQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public MyPetDto findOneMyPetByInGuardian(Long petId, String userId) {
        return queryFactory
                .select(Projections.fields(MyPetDto.class,
                        guardian.pet.id.as("petId"),
                        guardian.user.id.as("ownerId"),
                        guardian.repStatus,
                        pet.invitedCode,
                        pet.name,
                        pet.type,
                        pet.breed,
                        pet.gender,
                        pet.isNeutered,
                        pet.birth,
                        pet.firstMeetDate,
                        pet.weight,
                        pet.registeredNumber,
                        petImage.url.as("petImageUrl")
                ))
                .from(guardian)
                .innerJoin(pet).on(guardian.pet.id.eq(pet.id))
                .leftJoin(petImage).on(pet.id.eq(petImage.pet.id))
                .where(hasUserIdInGuardian(userId),
                        hasPetIdInPet(petId))
                .fetchOne();
    }

    public List<MyPetDto> findMyPetByInGuardian(String userId) {
        return queryFactory
                .select(Projections.fields(MyPetDto.class,
                        guardian.pet.id.as("petId"),
                        guardian.user.id.as("ownerId"),
                        guardian.repStatus,
                        pet.invitedCode,
                        pet.name,
                        pet.type,
                        pet.breed,
                        pet.gender,
                        pet.isNeutered,
                        pet.birth,
                        pet.firstMeetDate,
                        pet.weight,
                        pet.registeredNumber,
                        petImage.url.as("petImageUrl")
                ))
                .from(guardian)
                .innerJoin(pet).on(guardian.pet.id.eq(pet.id))
                .leftJoin(petImage).on(pet.id.eq(petImage.pet.id))
                .where(hasUserIdInGuardian(userId))
                .orderBy(guardian.createdAt.asc())
                .fetch();
    }

    private BooleanExpression hasUserIdInGuardian(String userId) {
        return guardian.user.id.eq(userId);
    }

    private BooleanExpression hasPetIdInPet(Long petId) {
        return pet.id.eq(petId);
    }

}
