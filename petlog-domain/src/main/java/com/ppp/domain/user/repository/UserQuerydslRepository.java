package com.ppp.domain.user.repository;

import com.ppp.domain.user.UserDao;
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

    public List<UserDao> findGuardianUserByPetId(Long petId) {
        return jpaQueryFactory.select(constructor(UserDao.class, user.id, user.nickname))
                .from(guardian)
                .leftJoin(guardian.user, user)
                .where(guardian.pet.id.eq(petId))
                .fetch();
    }
}
