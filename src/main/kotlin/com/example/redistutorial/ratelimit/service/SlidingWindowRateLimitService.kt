package com.example.redistutorial.ratelimit.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class SlidingWindowRateLimitService(
    private val redisTemplate: RedisTemplate<String,String>
) : RateLimitService {
    override fun isNeedToBlock(userId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun recordRequest(userId: String) {
        TODO("Not yet implemented")
    }
}