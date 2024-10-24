package com.example.apigwservice.common

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class HttpTraceRepositoryProvider {

    @Bean
    fun httpTraceRepository(): HttpExchangeRepository {
        return InMemoryHttpExchangeRepository()
    }
}
