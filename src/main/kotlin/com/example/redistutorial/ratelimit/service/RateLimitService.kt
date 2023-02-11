package com.example.redistutorial.ratelimit.service

import com.example.redistutorial.config.RateLimitKey
import java.time.Duration
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RateLimitService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(RateLimitService::class.java)
    private val BLOCK_THRESHHOLD_MINUTE = 5

    fun isNeedToBlock(userId: String): Boolean {
        val now = LocalDateTime.now()
        val keys = mutableListOf<String>()
        val kv = redisTemplate.opsForValue()

        for (i in 0 until BLOCK_THRESHHOLD_MINUTE) {
            keys.add(RateLimitKey.generate(userId, now.minusMinutes(i.toLong())))
        }

        val count =
            kv.multiGet(keys)
                ?.filterNotNull()
                ?.sumOf { requests -> requests.toInt() } ?: 0

        return count >= BLOCK_THRESHHOLD_MINUTE
    }

    fun recordRequest(userId: String) {
        val kv = redisTemplate.opsForValue()
        val key = RateLimitKey.generate(userId)

        kv.increment(key)
        redisTemplate.expire(key, Duration.ofMinutes(BLOCK_THRESHHOLD_MINUTE.toLong()))
    }
}