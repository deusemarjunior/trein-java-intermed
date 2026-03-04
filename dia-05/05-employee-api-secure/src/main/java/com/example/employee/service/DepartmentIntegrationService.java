package com.example.employee.service;

import com.example.employee.dto.ExternalDepartmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO 3: Configurar Resilience4j neste serviço.
 *
 * Passos:
 * 1. Descomentar a configuração do Resilience4j no application.yml
 * 2. Injetar o ExternalDepartmentClient (após completar TODO 1)
 * 3. Adicionar @Retry e @CircuitBreaker nos métodos
 * 4. Implementar os métodos de fallback
 *
 * Annotations a usar:
 *   @Retry(name = "departmentService", fallbackMethod = "findByIdFallback")
 *   @CircuitBreaker(name = "departmentService", fallbackMethod = "findByIdFallback")
 */
@Service
public class DepartmentIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentIntegrationService.class);

    // TODO 3: Injetar ExternalDepartmentClient
    // private final ExternalDepartmentClient departmentClient;

    // TODO 3: Adicionar @Retry e @CircuitBreaker
    public ExternalDepartmentResponse findById(Long id) {
        log.info("Buscando departamento id={} no serviço externo", id);

        // TODO 3: Chamar departmentClient.findById(id)
        // return departmentClient.findById(id);

        // Retorno temporário — remover após implementar
        return new ExternalDepartmentResponse(id, "Departamento Mock", "MOCK", "Implementar TODO 3");
    }

    // TODO 3: Adicionar @Retry e @CircuitBreaker
    public List<ExternalDepartmentResponse> findAll() {
        log.info("Buscando todos os departamentos no serviço externo");

        // TODO 3: Chamar departmentClient.findAll()
        // return departmentClient.findAll();

        // Retorno temporário — remover após implementar
        return List.of(
                new ExternalDepartmentResponse(0L, "Departamento Mock", "MOCK", "Implementar TODO 3")
        );
    }

    // TODO 3: Implementar métodos de fallback
    //
    // private ExternalDepartmentResponse findByIdFallback(Long id, Throwable t) {
    //     log.warn("Fallback ativado para departamento id={}: {}", id, t.getMessage());
    //     return new ExternalDepartmentResponse(id, "Departamento Indisponível", "N/A", "Fallback");
    // }
    //
    // private List<ExternalDepartmentResponse> findAllFallback(Throwable t) {
    //     log.warn("Fallback ativado para listagem de departamentos: {}", t.getMessage());
    //     return List.of(
    //             new ExternalDepartmentResponse(0L, "Serviço Indisponível", "N/A", "Fallback")
    //     );
    // }
}
