package com.example.redistutorial.ratelimit.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RateLimitController {

    @GetMapping("/hi")
    fun hi(): String {
        return "hi"
    }
}