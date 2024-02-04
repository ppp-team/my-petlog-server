package com.ppp.api.diary.service;

import com.ppp.common.client.RedisClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DiaryCommentRedisServiceTest {
    @Mock
    private RedisClient redisClient;

    @InjectMocks
    private DiaryCommentRedisService diaryCommentRedisService;

    @Test
    @DisplayName("다이어리 댓글 개수 조회 성공")
    void getDiaryCommentCountByDiaryId_success() {
        //given
        given(redisClient.getValue(any(), anyLong()))
                .willReturn(Optional.of("3"));
        //when
        Integer result = diaryCommentRedisService.getDiaryCommentCountByDiaryId(1L);
        //then
        assertEquals(result, 3);
    }

    @Test
    @DisplayName("다이어리 댓글 개수 조회 성공 - redis client 가 null 을 응답")
    void getDiaryCommentCountByDiaryId_success_whenRedisClientReturnNull() {
        //given
        given(redisClient.getValue(any(), anyLong()))
                .willReturn(Optional.empty());
        //when
        Integer result = diaryCommentRedisService.getDiaryCommentCountByDiaryId(1L);
        //then
        assertEquals(result, 0);
    }

}