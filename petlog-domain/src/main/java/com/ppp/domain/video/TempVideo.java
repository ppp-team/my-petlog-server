package com.ppp.domain.video;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "videos", timeToLive = 170L)
public class TempVideo {
    @Id
    private String id;
    @Column
    private String filePath;
    @Column
    private String userId;

    @Builder
    public TempVideo(String id, String filePath, String userId) {
        this.id = id;
        this.filePath = filePath;
        this.userId = userId;
    }
}
