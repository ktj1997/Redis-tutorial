package com.example.redistutorial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisTutorialApplication

fun main(args: Array<String>) {
    runApplication<RedisTutorialApplication>(*args)
}
