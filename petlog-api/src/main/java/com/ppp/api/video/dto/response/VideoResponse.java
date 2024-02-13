package com.ppp.api.video.dto.response;

import com.ppp.domain.video.TempVideo;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Builder
public record VideoResponse(
        String videoId,
        LocalDateTime validUntil
) {
    private final static long tempVideoValidSeconds = TempVideo.class.getAnnotation(RedisHash.class).timeToLive();

    public static VideoResponse from(TempVideo tempVideo) {
        return VideoResponse.builder()
                .videoId(tempVideo.getId())
                .validUntil(LocalDateTime.now().plusSeconds(tempVideoValidSeconds))
                .build();
    }
}
