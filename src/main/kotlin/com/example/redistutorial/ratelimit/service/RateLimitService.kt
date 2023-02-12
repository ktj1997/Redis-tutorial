package com.example.redistutorial.ratelimit.service

interface RateLimitService {
    fun isNeedToBlock(userId: String): Boolean

    fun recordRequest(userId: String)
}