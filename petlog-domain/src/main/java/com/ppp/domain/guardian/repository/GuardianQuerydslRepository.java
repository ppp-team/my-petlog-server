package com.ppp.domain.guardian.repository;

import com.ppp.domain.guardian.dto.MyPetResponseDto;
import com.querydsl.core.types.Projections;
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

    public List<MyPetResponseDto> findMyPetByInGuardian(String userId) {

        List<Long> petIds = queryFactory
                .select(pet.id)
                .from(guardian)
                .where(guardian.user.id.eq(userId))
                .fetch();

        return queryFactory
                .select(Projections.fields(MyPetResponseDto.class,
                        pet.id.as("petId"),
                        pet.user.id.as("ownerId"),
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
                        pet.repStatus,
                        petImage.url.as("petImageUrl")
                ))
                .from(pet)
                .leftJoin(petImage).on(pet.id.eq(petImage.pet.id))
                .where(pet.id.in(petIds))
                .orderBy(pet.createdAt.asc())
                .fetch();
    }
}
