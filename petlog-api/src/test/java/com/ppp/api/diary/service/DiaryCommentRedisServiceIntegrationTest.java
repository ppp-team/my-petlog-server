package com.ppp.api.diary.service;

import com.ppp.ApiApplication;
import com.ppp.common.client.FfmpegClient;
import com.ppp.common.client.RedisClient;
import com.ppp.common.config.FfmpegConfig;
import com.ppp.common.config.JasyptConfig;
import com.ppp.domain.common.constant.Domain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;
import java.util.Optional;

import static com.ppp.domain.common.constant.CacheValue.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApiApplication.class)
class DiaryCommentRedisServiceIntegrationTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DiaryCommentRedisService diaryCommentRedisService;

    @MockBean(JasyptConfig.class)
    private JasyptConfig jasyptConfig;

    @MockBean
    private RedisClient redisClient;

    @MockBean
    private FfmpegConfig ffmpegConfig;

    @MockBean
    private FfmpegClient ffmpegClient;

    @AfterEach
    void tearDown() {
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue()))
                .evictIfPresent("1");
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_LIKE_COUNT.getValue()))
                .evictIfPresent("1");
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .evictIfPresent("1");
    }

    @Test
    @DisplayName("다이어리 댓글 개수 캐싱 성공")
    void cachingGetDiaryCommentCountByDiaryId_success() {
        //given
        given(redisClient.getValue(Domain.DIARY_COMMENT, 1L))
                .willReturn(Optional.of("1"));
        //when
        Integer cacheMiss = diaryCommentRedisService.getDiaryCommentCountByDiaryId(1L);
        Integer cacheHit = diaryCommentRedisService.getDiaryCommentCountByDiaryId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        verify(redisClient, times(1)).getValue(any(), anyLong());
        assertEquals(cacheMiss, cacheHit);
        assertEquals(cacheMiss, cached);
    }

    @Test
    @DisplayName("다이어리 댓글 개수 캐싱 삭제 성공")
    void cachingDeleteDiaryCommentCountByDiaryId_success() {
        //given
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue())).put("1", 1);
        //when
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        diaryCommentRedisService.deleteDiaryCommentCountByDiaryId(1L);
        Integer deleted = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cached, 1);
        assertNull(deleted);
    }

    @Test
    @DisplayName("다이어리 댓글 개수 캐싱 업데이트 성공")
    void cachingIncreaseDiaryCommentCountByDiaryId_success() {
        //given
        given(redisClient.getValue(Domain.DIARY_COMMENT, 1L))
                .willReturn(Optional.of("1"));
        given(redisClient.incrementValue(Domain.DIARY_COMMENT, 1L))
                .willReturn(2L);
        //when
        diaryCommentRedisService.getDiaryCommentCountByDiaryId(1L);
        diaryCommentRedisService.increaseDiaryCommentCountByDiaryId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        //then
        assertEquals(2, cached);
    }

    @Test
    @DisplayName("다이어리 댓글 개수 캐싱 업데이트 성공")
    void cachingDecreaseDiaryCommentCountByDiaryId_success() {
        //given
        given(redisClient.getValue(Domain.DIARY_COMMENT, 1L))
                .willReturn(Optional.of("1"));
        given(redisClient.decrementValue(Domain.DIARY_COMMENT, 1L))
                .willReturn(0L);
        //when
        diaryCommentRedisService.getDiaryCommentCountByDiaryId(1L);
        diaryCommentRedisService.decreaseDiaryCommentCountByDiaryId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(0, cached);
    }

    @Test
    @DisplayName("다이어리 댓글 좋아요 개수 캐싱 성공")
    void cachingGetLikeCountByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, 1L))
                .willReturn(1L);
        //when
        Integer cacheMiss = diaryCommentRedisService.getLikeCountByCommentId(1L);
        Integer cacheHit = diaryCommentRedisService.getLikeCountByCommentId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        verify(redisClient, times(1)).getSizeOfSet(any(), anyLong());
        assertEquals(cacheMiss, cacheHit);
        assertEquals(cacheMiss, cached);
    }

    @Test
    @DisplayName("다이어리 댓글 좋아요 개수 캐싱 업데이트 성공")
    void cachingRegisterLikeByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, 1L))
                .willReturn(3L);
        //when
        Integer cacheUpdated = diaryCommentRedisService.registerLikeByCommentIdAndUserId(1L, "abcde");
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cacheUpdated, 3);
        assertEquals(cacheUpdated, cached);
    }

    @Test
    @DisplayName("다이어리 댓글 좋아요 개수 캐싱 업데이트 성공")
    void cachingCancelLikeByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, 1L))
                .willReturn(3L);
        //when
        Integer cacheUpdated = diaryCommentRedisService.cancelLikeByCommentIdAndUserId(1L, "abcde");
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cacheUpdated, 3);
        assertEquals(cacheUpdated, cached);
    }

    @Test
    @DisplayName("다이어리 댓글 좋아요 캐시 삭제 성공")
    void deleteAllLikeByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY_COMMENT_LIKE, 1L))
                .willReturn(3L);
        //when
        Integer cacheMiss = diaryCommentRedisService.getLikeCountByCommentId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_LIKE_COUNT.getValue()))
                .get("1", Integer.class);
        diaryCommentRedisService.deleteAllLikeByCommentId(1L);
        Integer deleted = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cacheMiss, 3);
        assertEquals(cached, 3);
        assertNull(deleted);
    }

    @Test
    @DisplayName("다이어리 대댓글 개수 캐싱 성공")
    void cachingGetDiaryReCommentCountByCommentId_success() {
        //given
        given(redisClient.getValue(Domain.DIARY_RE_COMMENT, 1L))
                .willReturn(Optional.of("1"));
        //when
        Integer cacheMiss = diaryCommentRedisService.getDiaryReCommentCountByCommentId(1L);
        Integer cacheHit = diaryCommentRedisService.getDiaryReCommentCountByCommentId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        verify(redisClient, times(1)).getValue(any(), anyLong());
        assertEquals(cacheMiss, cacheHit);
        assertEquals(cacheMiss, cached);
    }

    @Test
    @DisplayName("다이어리 대댓글 개수 캐싱 삭제 성공")
    void cachingDeleteDiaryReCommentCountByCommentId_success() {
        //given
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue())).put("1", 1);
        //when
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        diaryCommentRedisService.deleteDiaryReCommentCountByCommentId(1L);
        Integer deleted = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        //then
        assertEquals(cached, 1);
        assertNull(deleted);
    }

    @Test
    @DisplayName("다이어리 대댓글 개수 캐싱 업데이트 성공")
    void cachingIncreaseDiaryReCommentCountByCommentId_success() {
        //given
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue())).put("1", 1);
        given(redisClient.incrementValue(Domain.DIARY_RE_COMMENT, 1L))
                .willReturn(2L);
        //when
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        diaryCommentRedisService.increaseDiaryReCommentCountByCommentId(1L);
        Integer updated = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        //then
        assertEquals(cached, 1);
        assertEquals(updated, 2);
    }

    @Test
    @DisplayName("다이어리 대댓글 개수 캐싱 업데이트 성공")
    void cachingDecreaseDiaryReCommentCountByCommentId_success() {
        //given
        Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue())).put("1", 1);
        given(redisClient.decrementValue(Domain.DIARY_RE_COMMENT, 1L))
                .willReturn(0L);
        //when
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        diaryCommentRedisService.decreaseDiaryReCommentCountByCommentId(1L);
        Integer updated = Objects.requireNonNull(cacheManager.getCache(DIARY_COMMENT_RE_COMMENT_COUNT.getValue()))
                .get("1", Integer.class);
        //then
        assertEquals(cached, 1);
        assertEquals(updated, 0);
    }
}