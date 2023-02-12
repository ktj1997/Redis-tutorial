package com.example.redistutorial.ratelimit.service

import java.time.LocalDateTime

interface RateLimitService {
    companion object {
        const val BLOCK_THRESHHOLD_MINUTE = 5
        const val BLOCK_THRESHHOLD = 5
    }

    fun isNeedToBlock(userId: String, now: LocalDateTime): Boolean

    fun recordRequest(userId: String, now: LocalDateTime)
}