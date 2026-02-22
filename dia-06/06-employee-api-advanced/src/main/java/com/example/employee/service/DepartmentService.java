package com.example.employee.service;

import com.example.employee.exception.ResourceNotFoundException;
import com.example.employee.model.Department;
import com.example.employee.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// TODO 8: Importe as annotations de cache
// import org.springframework.cache.annotation.Cacheable;
// import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

/**
 * TODO 8: Adicione cache Redis nos métodos de leitura.
 *
 * Passos:
 * 1. Descomente @EnableCaching em EmployeeApiAdvancedApplication
 * 2. Mude spring.cache.type para "redis" no application.yml
 * 3. Adicione @Cacheable(value = "departments", key = "#id") no findById()
 * 4. Adicione @Cacheable(value = "departments", key = "'all'") no findAll()
 * 5. Adicione @CacheEvict(value = "departments", allEntries = true) no delete()
 *
 * Como verificar:
 * - 1ª chamada GET /api/departments/1 → log "CACHE MISS" aparece → buscou no banco
 * - 2ª chamada GET /api/departments/1 → log NÃO aparece → veio do cache Redis!
 */
@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // TODO 8a: Adicione @Cacheable(value = "departments", key = "#id")
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        log.info("🔍 [CACHE MISS] Buscando departamento #{} no banco de dados...", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    // TODO 8b: Adicione @Cacheable(value = "departments", key = "'all'")
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        log.info("🔍 [CACHE MISS] Buscando todos os departamentos no banco de dados...");
        return departmentRepository.findAll();
    }

    // TODO 8c: Adicione @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", id);
        }
        departmentRepository.deleteById(id);
        log.info("🗑️ Departamento #{} deletado", id);
    }
}
