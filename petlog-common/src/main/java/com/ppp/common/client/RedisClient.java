package com.ppp.common.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisClient {
    private final RedisTemplate<String, String> redisTemplate;

    public void addValueToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void removeValueToSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public boolean isValueExistInSet(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    public Long getSizeOfSet(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public void removeKeyToSet(String key) {
        redisTemplate.opsForSet().pop(key);
    }
}
