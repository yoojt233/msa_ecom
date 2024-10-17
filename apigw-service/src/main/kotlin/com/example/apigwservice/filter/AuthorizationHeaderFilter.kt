package com.example.apigwservice.filter

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthorizationHeaderFilter(private val env: Environment) :
    AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>(Config::class.java) {
    private val logger = LoggerFactory.getLogger(AuthorizationHeaderFilter::class.java)

    class Config {}

    // login -> token -> users (with token) -> header(include token)
    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request

            if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return@GatewayFilter onError(exchange, "no authorization header found", HttpStatus.UNAUTHORIZED)
            }

            val authorizationHeader = request.headers[HttpHeaders.AUTHORIZATION]!!.first()
            val jwt = authorizationHeader.replace("Bearer ", "")

            if (!isJwtValid(jwt)) {
                return@GatewayFilter onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED)
            }

            chain.filter(exchange)
        }
    }

    private fun isJwtValid(jwt: String): Boolean {
        var res = true

        val secretKeyBytes = Base64.getEncoder().encode(env.getProperty("token.secret")!!.toByteArray())
        val signingKey = Keys.hmacShaKeyFor(secretKeyBytes)

        val subject =
            try {
                Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(jwt).payload.subject
            } catch (e: Exception) {
                res = false
                ""
            }

        if (subject == null || subject.isEmpty() || subject.isBlank()) {
            res = false
        }

        return res
    }

    // Mono, Flux -> Spring WebFlux
    private fun onError(exchange: ServerWebExchange, message: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response

        response.statusCode = httpStatus
        logger.error(message)

        return response.setComplete()
    }
}