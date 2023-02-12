package com.example.redistutorial.ratelimit.filter

import com.example.redistutorial.ratelimit.service.RateLimitException
import com.example.redistutorial.ratelimit.service.FixedWindowRateLimitService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.lang.Exception
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

@Profile("ratelimit")
class RateLimitFilter(
    private val fixedWindowRateLimitService: FixedWindowRateLimitService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val userIdentifier = request.getHeader("X-UserId")
                ?: throw IllegalArgumentException("No User Identifier Found")
            if (fixedWindowRateLimitService.isNeedToBlock(userIdentifier)) {
                throw IllegalArgumentException("Too Many Requests")
            }
            fixedWindowRateLimitService.recordRequest(userIdentifier)
            filterChain.doFilter(request, response)
        } catch (e: RateLimitException) {
            sendErrorMessage(response as HttpServletResponse, HttpStatus.TOO_MANY_REQUESTS, e)
        } catch (e: IllegalArgumentException) {
            sendErrorMessage(response as HttpServletResponse, HttpStatus.BAD_REQUEST, e)
        } catch (e: Exception) {
            sendErrorMessage(response as HttpServletResponse, HttpStatus.INTERNAL_SERVER_ERROR, e)
        }
    }

    private fun sendErrorMessage(res: HttpServletResponse, status: HttpStatus, error: Exception) {
        res.status = status.value()
        res.contentType = MediaType.TEXT_PLAIN_VALUE
        res.characterEncoding = "UTF-8"
        error.message?.let { res.writer.write(it) }
    }
}