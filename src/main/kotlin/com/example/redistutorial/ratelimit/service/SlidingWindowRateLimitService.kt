package com.example.redistutorial.ratelimit.service

import com.example.redistutorial.config.SlidingWindowRateLimitKey
import com.example.redistutorial.ratelimit.service.RateLimitService.Companion.BLOCK_THRESHHOLD
import com.example.redistutorial.ratelimit.service.RateLimitService.Companion.BLOCK_THRESHHOLD_MINUTE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class SlidingWindowRateLimitService(
    private val redisTemplate: RedisTemplate<String, String>
) : RateLimitService {
    override fun isNeedToBlock(userId: String, now: LocalDateTime): Boolean {
        val zSet = redisTemplate.opsForZSet()
        val key = SlidingWindowRateLimitKey.generate(userId)

        val maxTimeStamp = getDoubleTimeStamp(now)
        val minTimeStamp = getDoubleTimeStamp(now.minusMinutes(BLOCK_THRESHHOLD_MINUTE.toLong()))

        val recentRequestCount = zSet.count(key, minTimeStamp, maxTimeStamp) ?: 0

        return recentRequestCount >= BLOCK_THRESHHOLD
    }

    override fun recordRequest(userId: String, now: LocalDateTime) {

        val zSet = redisTemplate.opsForZSet()
        val key = SlidingWindowRateLimitKey.generate(userId)

        val nowTimestamp = getDoubleTimeStamp(now)
        val minTimeStamp = getDoubleTimeStamp(now.minusMinutes(BLOCK_THRESHHOLD_MINUTE.toLong()))

        zSet.removeRangeByScore(key, Double.NEGATIVE_INFINITY, minTimeStamp)
        zSet.add(key, "$userId > $now", nowTimestamp)

    }

    private fun getDoubleTimeStamp(timeStamp: LocalDateTime): Double {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MMddHHmmss")
        val string = formatter.format(timeStamp)

        return string.toDouble()
    }

}