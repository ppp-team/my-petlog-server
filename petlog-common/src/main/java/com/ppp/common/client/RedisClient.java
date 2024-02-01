package com.ppp.common.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

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

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
