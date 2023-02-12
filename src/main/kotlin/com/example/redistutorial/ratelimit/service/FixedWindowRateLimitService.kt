package com.example.redistutorial.ratelimit.service

import com.example.redistutorial.config.FixedRateLimitKey
import java.time.Duration
import java.time.LocalDateTime
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class FixedWindowRateLimitService(
    private val redisTemplate: RedisTemplate<String, String>
) : RateLimitService {
    private val BLOCK_THRESHHOLD_MINUTE = 5

    override fun isNeedToBlock(userId: String): Boolean {
        val now = LocalDateTime.now()
        val keys = mutableListOf<String>()
        val kv = redisTemplate.opsForValue()

        for (i in 0 until BLOCK_THRESHHOLD_MINUTE) {
            keys.add(FixedRateLimitKey.generate(userId, now.minusMinutes(i.toLong())))
        }

        val count =
            kv.multiGet(keys)
                ?.filterNotNull()
                ?.sumOf { requests -> requests.toInt() } ?: 0

        return count >= BLOCK_THRESHHOLD_MINUTE
    }

    override fun recordRequest(userId: String) {
        val kv = redisTemplate.opsForValue()
        val key = FixedRateLimitKey.generate(userId)

        kv.increment(key)
        redisTemplate.expire(key, Duration.ofMinutes(BLOCK_THRESHHOLD_MINUTE.toLong()))
    }
}