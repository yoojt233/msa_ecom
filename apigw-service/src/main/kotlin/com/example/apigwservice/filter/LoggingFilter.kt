package com.example.apigwservice.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class LoggingFilter : AbstractGatewayFilterFactory<LoggingFilter.Config>(Config::class.java) {
    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    data class Config(var baseMessage: String? = null, var preLogger: Boolean = false, var postLogger: Boolean = false)

    override fun apply(config: Config?): GatewayFilter {
        val filter: GatewayFilter = OrderedGatewayFilter({ exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            val response = exchange.response

            if (config != null) {
                logger.info("Logging Filter baseMessage: {}", config.baseMessage)
            }

            if (config != null) {
                if (config.preLogger) logger.info("Logging PRE Filter: request id -> {}", request.id)
            }

            chain.filter(exchange).then(Mono.fromRunnable {
                if (config != null) {
                    if (config.postLogger) logger.info("Logging POST Filter: response id -> {}", response.statusCode)
                }
            })
        }, Ordered.LOWEST_PRECEDENCE)

        return filter
    }
}
