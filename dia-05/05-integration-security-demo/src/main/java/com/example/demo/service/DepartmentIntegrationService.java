package com.example.demo.service;

import com.example.demo.dto.DepartmentResponse;
import com.example.demo.client.DepartmentClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentIntegrationService.class);

    private final DepartmentClient departmentClient;

    public DepartmentIntegrationService(DepartmentClient departmentClient) {
        this.departmentClient = departmentClient;
    }

    @Retry(name = "departmentService", fallbackMethod = "findByIdFallback")
    @CircuitBreaker(name = "departmentService", fallbackMethod = "findByIdFallback")
    public DepartmentResponse findById(Long id) {
        log.info("Buscando departamento id={} no serviço externo", id);
        return departmentClient.findById(id);
    }

    @Retry(name = "departmentService", fallbackMethod = "findAllFallback")
    @CircuitBreaker(name = "departmentService", fallbackMethod = "findAllFallback")
    public List<DepartmentResponse> findAll() {
        log.info("Buscando todos os departamentos no serviço externo");
        return departmentClient.findAll();
    }

    // ── Fallbacks ──────────────────────────────────────────────

    private DepartmentResponse findByIdFallback(Long id, Throwable t) {
        log.warn("Fallback ativado para departamento id={}: {}", id, t.getMessage());
        return new DepartmentResponse(id, "Departamento Indisponível", "N/A");
    }

    private List<DepartmentResponse> findAllFallback(Throwable t) {
        log.warn("Fallback ativado para listagem de departamentos: {}", t.getMessage());
        return List.of(
                new DepartmentResponse(0L, "Serviço Indisponível", "N/A")
        );
    }
}
