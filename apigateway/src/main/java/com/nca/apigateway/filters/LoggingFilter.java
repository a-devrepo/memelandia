package com.nca.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Lógica antes de enviar para o microserviço (Pre-filter)
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Lógica após receber a resposta do microserviço (Post-filter)
            // Aqui o TraceID já estará preenchido no log automaticamente!
            logger.info("Finalizado: {} {} | Status: {}",
                    method, path, exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        // Define a prioridade.
        // Ordered.LOWEST_PRECEDENCE garante que ele logue após todos os outros filtros.
        return Ordered.LOWEST_PRECEDENCE;
    }
}