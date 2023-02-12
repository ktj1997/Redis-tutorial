package com.example.redistutorial.ratelimit.service

import com.example.redistutorial.config.FixedWindowRateLimitKey
import com.example.redistutorial.ratelimit.service.RateLimitService.Companion.BLOCK_THRESHHOLD
import com.example.redistutorial.ratelimit.service.RateLimitService.Companion.BLOCK_THRESHHOLD_MINUTE
import java.time.Duration
import java.time.LocalDateTime
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class FixedWindowRateLimitService(
    private val redisTemplate: RedisTemplate<String, String>
) : RateLimitService {
    override fun isNeedToBlock(userId: String, now: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        val keys = mutableListOf<String>()
        val kv = redisTemplate.opsForValue()

        for (i in 0 until BLOCK_THRESHHOLD_MINUTE) {
            keys.add(FixedWindowRateLimitKey.generate(userId, now.minusMinutes(i.toLong())))
        }

        val count =
            kv.multiGet(keys)
                ?.filterNotNull()
                ?.sumOf { requests -> requests.toInt() } ?: 0

        return count >= BLOCK_THRESHHOLD
    }

    override fun recordRequest(userId: String, now: LocalDateTime) {
        val kv = redisTemplate.opsForValue()
        val key = FixedWindowRateLimitKey.generate(userId)

        kv.increment(key)
        redisTemplate.expire(key, Duration.ofMinutes(BLOCK_THRESHHOLD_MINUTE.toLong()))
    }
}