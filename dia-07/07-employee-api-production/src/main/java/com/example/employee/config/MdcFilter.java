package com.example.employee.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * TODO 6b: Implemente o filtro MdcFilter.
 *
 * Este filtro HTTP deve:
 * 1. Gerar um traceId único (UUID.randomUUID().toString().substring(0, 8))
 * 2. Adicionar ao MDC: traceId, method (GET/POST/etc), uri (/api/employees)
 * 3. Chamar chain.doFilter(request, response)
 * 4. Limpar o MDC no bloco finally (MDC.clear())
 *
 * Exemplo de implementação:
 *
 *   @Override
 *   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
 *           throws IOException, ServletException {
 *       HttpServletRequest httpRequest = (HttpServletRequest) request;
 *       try {
 *           String traceId = UUID.randomUUID().toString().substring(0, 8);
 *           MDC.put("traceId", traceId);
 *           MDC.put("method", httpRequest.getMethod());
 *           MDC.put("uri", httpRequest.getRequestURI());
 *           chain.doFilter(request, response);
 *       } finally {
 *           MDC.clear();
 *       }
 *   }
 */
@Component
@Order(1)
public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // TODO 6b: Implemente o filtro conforme descrito no Javadoc acima
        //
        // HttpServletRequest httpRequest = (HttpServletRequest) request;
        // try {
        //     String traceId = UUID.randomUUID().toString().substring(0, 8);
        //     MDC.put("traceId", traceId);
        //     MDC.put("method", httpRequest.getMethod());
        //     MDC.put("uri", httpRequest.getRequestURI());
        //     chain.doFilter(request, response);
        // } finally {
        //     MDC.clear();
        // }

        // Versão sem MDC (apenas passa adiante)
        chain.doFilter(request, response);
    }
}
