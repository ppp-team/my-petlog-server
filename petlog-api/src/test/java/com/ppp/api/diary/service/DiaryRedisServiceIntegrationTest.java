package com.ppp.api.diary.service;

import com.ppp.ApiApplication;
import com.ppp.common.client.RedisClient;
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

import static com.ppp.domain.common.constant.CacheValue.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApiApplication.class)
class DiaryRedisServiceIntegrationTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DiaryRedisService diaryRedisService;

    @MockBean(JasyptConfig.class)
    private JasyptConfig jasyptConfig;

    @MockBean
    private RedisClient redisClient;

    @AfterEach
    void tearDown() {
        Objects.requireNonNull(cacheManager.getCache(DIARY_LIKE_COUNT.getValue()))
                .evictIfPresent("1");
    }

    @Test
    @DisplayName("다이어리 좋아요 개수 캐싱 성공")
    void cachingGetLikeCountByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY, 1L))
                .willReturn(1L);
        //when
        Integer cacheMiss = diaryRedisService.getLikeCountByDiaryId(1L);
        Integer cacheHit = diaryRedisService.getLikeCountByDiaryId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        verify(redisClient, times(1)).getSizeOfSet(any(), anyLong());
        assertEquals(cacheMiss, cacheHit);
        assertEquals(cacheMiss, cached);
    }

    @Test
    @DisplayName("다이어리 좋아요 개수 캐싱 업데이트 성공")
    void cachingRegisterLikeByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY, 1L))
                .willReturn(3L);
        //when
        Integer cacheUpdated = diaryRedisService.registerLikeByDiaryIdAndUserId(1L, "abcde");
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cacheUpdated, 3);
        assertEquals(cacheUpdated, cached);
    }

    @Test
    @DisplayName("다이어리 좋아요 개수 캐싱 업데이트 성공")
    void cachingCancelLikeByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY, 1L))
                .willReturn(3L);
        //when
        Integer cacheUpdated = diaryRedisService.cancelLikeByDiaryIdAndUserId(1L, "abcde");
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cacheUpdated, 3);
        assertEquals(cacheUpdated, cached);
    }

    @Test
    @DisplayName("다이어리 좋아요 캐시 삭제 성공")
    void deleteAllLikeByCommentId_success() {
        //given
        given(redisClient.getSizeOfSet(Domain.DIARY, 1L))
                .willReturn(3L);
        //when
        Integer cacheMiss = diaryRedisService.getLikeCountByDiaryId(1L);
        Integer cached = Objects.requireNonNull(cacheManager.getCache(DIARY_LIKE_COUNT.getValue()))
                .get("1", Integer.class);
        diaryRedisService.deleteAllLikeByDiaryId(1L);
        Integer deleted = Objects.requireNonNull(cacheManager.getCache(DIARY_LIKE_COUNT.getValue()))
                .get("1", Integer.class);

        //then
        assertEquals(cacheMiss, 3);
        assertEquals(cached, 3);
        assertNull(deleted);
    }

}