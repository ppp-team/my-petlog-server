package com.ppp.domain.log.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.ppp.domain.log.QLog.log;

@Repository
@RequiredArgsConstructor
public class LogQuerydslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<LocalDate> findExistingDayByPetIdInMonth(Long petId, LocalDate theMonth) {
        return jpaQueryFactory.select(dateTimeToDate().as(aliasDate()))
                .from(log)
                .where(petIdEq(petId), dateTimeIsIncludedInTheMonth(theMonth))
                .groupBy(dateTimeToDate())
                .orderBy(aliasDate().asc())
                .fetch()
                .stream()
                .map(Date::toLocalDate).collect(Collectors.toList());
    }

    private DatePath<Date> aliasDate() {
        return Expressions.datePath(Date.class, "day");
    }

    private DateTemplate<Date> dateTimeToDate() {
        return Expressions.dateTemplate(
                Date.class,
                "DATE({0})",
                log.datetime);
    }

    private BooleanExpression dateTimeIsIncludedInTheMonth(LocalDate theMonth) {
        return log.datetime.between(firstDayOfMonth(theMonth), firstDayOfMonth(theMonth.plusMonths(1)));
    }

    private DateTemplate firstDayOfMonth(LocalDate date) {
        return Expressions.dateTemplate(
                Date.class,
                "DATE_FORMAT({0}, {1})",
                date,
                ConstantImpl.create("%Y-%m-01"));
    }

    private BooleanExpression petIdEq(Long petId) {
        return petId != null ? log.pet.id.eq(petId) : null;
    }

}
