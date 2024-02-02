package com.ppp.api.diary.service;

import com.ppp.common.client.RedisClient;
import com.ppp.domain.common.constant.Domain;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiaryCommentCountRedisService {
    private final RedisClient redisClient;

    @Cacheable(value = "diaryCommentCount")
    public Integer getDiaryCommentCountByDiaryId(Long diaryId) {
        return redisClient.getValue(Domain.DIARY_COMMENT, diaryId)
                .map(Integer::parseInt)
                .orElse(0);
    }

    public void setDiaryCommentCountByDiaryId(Long diaryId) {
        redisClient.addValue(Domain.DIARY_COMMENT, diaryId, "0");
    }

    @CacheEvict(value = "diaryCommentCount")
    public void deleteDiaryCommentCountByDiaryId(Long diaryId) {
        redisClient.deleteValue(Domain.DIARY_COMMENT, diaryId);
    }

    @CachePut(value = "diaryCommentCount", unless = "#result == null")
    public void increaseDiaryCommentCountByDiaryId(Long diaryId) {
        redisClient.incrementValue(Domain.DIARY_COMMENT, diaryId);
    }

    @CachePut(value = "diaryCommentCount", unless = "#result == null")
    public void decreaseDiaryCommentCountByDiaryId(Long diaryId) {
        redisClient.decrementValue(Domain.DIARY_COMMENT, diaryId);
    }
}
