package com.samratalam.ewallet_system.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setKey(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setKeyWithTime(String key, Object value, Long valueOfTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, valueOfTime, timeUnit);
    }

    public boolean setKeyIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public boolean setKeyWithTimeIfAbsent(String key, Object value, Long valueOfTime, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, valueOfTime, timeUnit);
    }


    public Object getKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean removeKey(String key) {
        return redisTemplate.delete(key);
    }
}
