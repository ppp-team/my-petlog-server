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
public class DiaryCommentRedisService {
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
    public Long increaseDiaryCommentCountByDiaryId(Long diaryId) {
        return redisClient.incrementValue(Domain.DIARY_COMMENT, diaryId);
    }

    @CachePut(value = "diaryCommentCount", unless = "#result == null")
    public Long decreaseDiaryCommentCountByDiaryId(Long diaryId) {
        return redisClient.decrementValue(Domain.DIARY_COMMENT, diaryId);
    }

    public boolean isDiaryCommentLikeExistByCommentIdAndUserId(Long commentId, String userId) {
        return redisClient.isValueExistInSet(Domain.DIARY_COMMENT_LIKE, commentId, userId);
    }

    @Cacheable(value = "diaryCommentLikeCount")
    public Integer getLikeCountByCommentId(Long commentId) {
        Long likeCount = redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, commentId);
        assert likeCount != null;

        return likeCount.intValue();
    }

    @CachePut(value = "diaryCommentLikeCount", key = "#a0")
    public Integer registerLikeByCommentIdAndUserId(Long commentId, String userId) {
        redisClient.addValueToSet(Domain.DIARY_COMMENT_LIKE, commentId, userId);
        Long likeCount = redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, commentId);
        assert likeCount != null;

        return likeCount.intValue();
    }

    @CachePut(value = "diaryCommentLikeCount", key = "#a0")
    public Integer cancelLikeByCommentIdAndUserId(Long commentId, String userId) {
        redisClient.removeValueToSet(Domain.DIARY_COMMENT_LIKE, commentId, userId);
        Long likeCount = redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, commentId);
        assert likeCount != null;

        return likeCount.intValue();
    }

    @CacheEvict(value = "diaryCommentLikeCount")
    public void deleteAllLikeByCommentId(Long commentId) {
        redisClient.removeKeyToSet(Domain.DIARY_COMMENT_LIKE, commentId);
    }
}
