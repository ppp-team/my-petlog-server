package com.ppp.domain.user.repository;

import com.ppp.domain.user.dto.UserDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ppp.domain.guardian.QGuardian.guardian;
import static com.ppp.domain.user.QUser.user;
import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class UserQuerydslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<UserDto> findGuardianUserByPetId(Long petId) {
        return jpaQueryFactory.select(constructor(UserDto.class, user.id, user.nickname))
                .from(guardian)
                .leftJoin(guardian.user, user)
                .where(petIdEq(petId))
                .fetch();
    }

    private BooleanExpression petIdEq(Long petId) {
        return petId != null ? guardian.pet.id.eq(petId) : null;
    }
}
