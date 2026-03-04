package com.example.employee.service;

import com.example.employee.exception.ResourceNotFoundException;
import com.example.employee.model.Department;
import com.example.employee.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Cacheable(value = "departments", key = "#id")
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        log.info("[CACHE MISS] Buscando departamento #{} no banco de dados...", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    @Cacheable(value = "departments", key = "'all'")
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        log.info("[CACHE MISS] Buscando todos os departamentos no banco de dados...");
        return departmentRepository.findAll();
    }

    @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", id);
        }
        departmentRepository.deleteById(id);
        log.info("Departamento #{} deletado", id);
    }
}
