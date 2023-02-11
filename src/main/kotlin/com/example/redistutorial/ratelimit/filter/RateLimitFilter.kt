package com.example.redistutorial.ratelimit.filter

import com.example.redistutorial.ratelimit.service.RateLimitException
import com.example.redistutorial.ratelimit.service.RateLimitService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.awt.PageAttributes
import java.lang.Exception
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
@Profile("ratelimit")
class RateLimitFilter(
    private val rateLimitService: RateLimitService
) : Filter {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        try {
            (request as HttpServletRequest).let { req ->
                val userIdentifier = req.getHeader("X-UserId")
                    ?: throw IllegalArgumentException("No User Identifier Found")

                if (rateLimitService.isNeedToBlock(userIdentifier)) {
                    throw IllegalArgumentException("Too Many Requests")
                }

                rateLimitService.recordRequest(userIdentifier)
                chain.doFilter(request, response)
            }
        } catch (e : RateLimitException){
            sendErrorMessage(response as HttpServletResponse, HttpStatus.TOO_MANY_REQUESTS,e)
        }catch (e: IllegalArgumentException){
            sendErrorMessage(response as HttpServletResponse, HttpStatus.BAD_REQUEST, e)
        }catch (e: Exception){
            sendErrorMessage(response as HttpServletResponse, HttpStatus.INTERNAL_SERVER_ERROR,e)
        }
    }

    private fun sendErrorMessage(res: HttpServletResponse, status: HttpStatus, error: Exception) {
        res.status = status.value()
        res.contentType = MediaType.TEXT_PLAIN_VALUE
        res.characterEncoding = "UTF-8"
        error.message?.let { res.writer.write(it) }
    }
}