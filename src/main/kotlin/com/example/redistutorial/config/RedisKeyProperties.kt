package com.example.redistutorial.config

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RateLimitKey{
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
    fun generate(userId: String, date: LocalDateTime = LocalDateTime.now()): String {
        return "rate_limit:user:${userId}:${formatter.format(date)}"
    }
}
