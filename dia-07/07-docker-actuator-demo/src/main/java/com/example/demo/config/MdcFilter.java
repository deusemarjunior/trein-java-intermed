package com.example.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro HTTP que adiciona traceId, method e uri ao MDC.
 *
 * Esses campos aparecem automaticamente em cada linha de log JSON,
 * permitindo correlacionar todos os logs de uma mesma requisição.
 */
@Component
@Order(1)
public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            // Gerar traceId único para esta requisição
            String traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);
            MDC.put("method", httpRequest.getMethod());
            MDC.put("uri", httpRequest.getRequestURI());

            chain.doFilter(request, response);
        } finally {
            // Limpar MDC para evitar vazamento entre requests
            MDC.clear();
        }
    }
}
