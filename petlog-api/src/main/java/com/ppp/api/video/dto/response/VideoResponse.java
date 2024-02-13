package com.ppp.api.video.dto.response;

import com.ppp.domain.video.TempVideo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Schema(description = "임시 동영상")
@Builder
public record VideoResponse(
        @Schema(description = "임시 동영상 아이디")
        String videoId,
        @Schema(description = "임시 동영상 유효 시간")
        LocalDateTime validUntil
) {
    private final static long tempVideoValidMinutes = TempVideo.class.getAnnotation(RedisHash.class).timeToLive();

    public static VideoResponse from(TempVideo tempVideo) {
        return VideoResponse.builder()
                .videoId(tempVideo.getId())
                .validUntil(LocalDateTime.now().plusMinutes(tempVideoValidMinutes))
                .build();
    }
}
