package com.example.apigwservice.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GlobalFilter : AbstractGatewayFilterFactory<GlobalFilter.Config>(Config::class.java) {
    private val logger = LoggerFactory.getLogger(GlobalFilter::class.java)

    data class Config(var baseMessage: String? = null, var preLogger: Boolean = false, var postLogger: Boolean = false)

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response

            if (config != null) {
                logger.info("Global Filter baseMassage: {}", config.baseMessage)
            }


            if (config != null) {
                if (config.preLogger) logger.info("Global Filter Start: request id -> ${request.id}")
            }

            chain.filter(exchange).then(Mono.fromRunnable {
                if (config != null) {
                    if (config.postLogger) logger.info("PostLogger: ${response.statusCode}")
                }
            })
        }
    }
}
