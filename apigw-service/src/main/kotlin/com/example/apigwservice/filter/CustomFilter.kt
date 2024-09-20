package com.example.apigwservice.filter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomFilter : AbstractGatewayFilterFactory<CustomFilter.Config>(Config::class.java) {
    private val logger: Logger = LoggerFactory.getLogger(CustomFilter::class.java)

    class Config {
        // TODO
    }

    override fun apply(config: Config?): GatewayFilter {

        // Custom Pre Filter
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response

            logger.info("Custom PRE filter: request uri -> ${request.id}")

            // Custom Post filter
            chain.filter(exchange).then(Mono.fromRunnable {
                logger.info("Custom POST filter: response code -> ${response.statusCode}")
            })
        }
    }
}
