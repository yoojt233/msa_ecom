package com.example.apigwservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public class temp extends AbstractGatewayFilterFactory<temp.Config> {
    private final Logger logger = LoggerFactory.getLogger(temp.class);

    public temp() {
        super(Config.class);
    }

    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

        public String getBaseMessage() {
            return this.baseMessage;
        }

        public boolean isPreLogger() {
            return this.preLogger;
        }

        public boolean isPostLogger() {
            return this.postLogger;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            logger.info("Logging Filter baseMessage: {}", config.getBaseMessage());
            if (config.isPreLogger()) logger.info("Logging PRE Filter: request id -> {}", request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger())
                    logger.info("Logging POST Filter: response id -> {}", response.getStatusCode());
            }));
        }, Ordered.HIGHEST_PRECEDENCE);

        return filter;
    }
}
