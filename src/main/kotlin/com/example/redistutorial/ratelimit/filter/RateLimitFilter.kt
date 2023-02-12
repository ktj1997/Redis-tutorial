package com.example.redistutorial.ratelimit.filter

import com.example.redistutorial.ratelimit.service.RateLimitException
import com.example.redistutorial.ratelimit.service.RateLimitService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.LocalDateTime
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

@Profile("ratelimit")
class RateLimitFilter(
    private val rateLimitService: RateLimitService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val now = LocalDateTime.now()
            val userIdentifier = request.getHeader("X-UserId")
                ?: throw IllegalArgumentException("No User Identifier Found")
            if (rateLimitService.isNeedToBlock(userIdentifier, now)) {
                throw IllegalArgumentException("Too Many Requests")
            }
            rateLimitService.recordRequest(userIdentifier, now)
            filterChain.doFilter(request, response)
        } catch (e: RateLimitException) {
            sendErrorMessage(response, HttpStatus.TOO_MANY_REQUESTS, e)
        } catch (e: IllegalArgumentException) {
            sendErrorMessage(response, HttpStatus.BAD_REQUEST, e)
        } catch (e: Exception) {
            sendErrorMessage(response, HttpStatus.INTERNAL_SERVER_ERROR, e)
        }
    }

    private fun sendErrorMessage(res: HttpServletResponse, status: HttpStatus, error: Exception) {
        res.status = status.value()
        res.contentType = MediaType.TEXT_PLAIN_VALUE
        res.characterEncoding = "UTF-8"
        error.message?.let { res.writer.write(it) }
    }
}