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
        return queryFactory
                .select(Projections.fields(MyPetResponseDto.class,
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
                .where(guardian.user.id.eq(userId))
                .orderBy(guardian.createdAt.asc())
                .fetch();
    }
}
