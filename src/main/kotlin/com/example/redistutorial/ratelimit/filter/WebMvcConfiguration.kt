package com.example.redistutorial.ratelimit.filter

import com.example.redistutorial.ratelimit.service.FixedWindowRateLimitService
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@Profile("ratelimit")
class WebMvcConfiguration : WebMvcConfigurer {

    @Bean
    fun practiceFilterRegistrationBean(fixedWindowRateLimitService: FixedWindowRateLimitService): FilterRegistrationBean<*> {
        val filterRegistrationBean: FilterRegistrationBean<RateLimitFilter> =
            FilterRegistrationBean<RateLimitFilter>(RateLimitFilter(fixedWindowRateLimitService))
        filterRegistrationBean.order = Ordered.LOWEST_PRECEDENCE
        filterRegistrationBean.urlPatterns = setOf("/api/*")
        return filterRegistrationBean
    }
}