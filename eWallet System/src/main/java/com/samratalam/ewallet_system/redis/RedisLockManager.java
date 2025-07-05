package com.samratalam.ewallet_system.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLockManager {
    private static final long LOCK_TIMEOUT_MIN = 2;
    @Autowired
    private RedisRepository redisRepository;

    public String acquireLock(String lockName, String lockValue) {
        var success = redisRepository.setKeyWithTimeIfAbsent(lockName, lockValue, LOCK_TIMEOUT_MIN, TimeUnit.MINUTES);
        return Boolean.TRUE.equals(success) ? lockValue : null;
    }

    public void releaseLock(String lockName, String lockValue) {
        if (lockValue.equals(redisRepository.getKey(lockName))) {
            redisRepository.removeKey(lockName);
        }
    }
}
