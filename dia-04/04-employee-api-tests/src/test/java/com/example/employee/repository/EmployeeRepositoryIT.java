package com.example.employee.repository;

import com.example.employee.AbstractIntegrationTest;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link EmployeeRepository} com Testcontainers.
 *
 * <p>Exercícios TODO 6 e 7 — implemente os testes de integração.</p>
 *
 * <p><strong>Pré-requisito:</strong> Docker Desktop deve estar rodando.</p>
 */
@DisplayName("EmployeeRepository — Testes de Integração")
class EmployeeRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        department = departmentRepository.save(new Department(null, "Engenharia"));
    }

    // ====================================================================
    // TODO 6: Testes de integração — save e findById
    // ====================================================================

    /**
     * TODO 6: Implementar teste de salvar e recuperar funcionário
     *
     * Passos:
     * 1. Criar um Employee com todos os campos
     * 2. Salvar com repository.save()
     * 3. Buscar com repository.findById()
     * 4. Verificar que:
     *    - O resultado está presente (isPresent)
     *    - name, email, cpf, salary estão corretos
     *    - createdAt não é null
     *    - department.name está correto
     */
    @Test
    @DisplayName("deve salvar e recuperar funcionário por ID")
    @Disabled("TODO 6: Implementar este teste")
    void shouldSaveAndFindById() {
        // Arrange
        // TODO 6: Criar Employee com new Employee("Ana Silva", "ana@email.com",
        //         "123.456.789-09", new BigDecimal("5000.00"), department)

        // Act
        // TODO 6: Salvar e buscar por ID

        // Assert
        // TODO 6: Verificar campos com assertThat
    }

    /**
     * TODO 6: Implementar teste de busca por email
     *
     * Passos:
     * 1. Salvar um funcionário
     * 2. Buscar com repository.findByEmail()
     * 3. Verificar que encontrou o funcionário correto
     */
    @Test
    @DisplayName("deve encontrar funcionário por email")
    @Disabled("TODO 6: Implementar este teste")
    void shouldFindByEmail() {
        // Arrange
        // TODO 6: Criar e salvar Employee

        // Act
        // TODO 6: Buscar por email

        // Assert
        // TODO 6: Verificar que está presente e tem o nome correto
    }

    // ====================================================================
    // TODO 7: Teste de UNIQUE constraint no email
    // ====================================================================

    /**
     * TODO 7: Implementar teste de restrição UNIQUE no email
     *
     * Passos:
     * 1. Salvar um funcionário com email "ana@email.com" usando saveAndFlush()
     * 2. Criar OUTRO funcionário com o MESMO email
     * 3. Tentar salvar com saveAndFlush()
     * 4. Verificar que lança uma Exception (constraint violation)
     *
     * Dica: use assertThatThrownBy(() -> repository.saveAndFlush(employee2))
     *           .isInstanceOf(Exception.class);
     */
    @Test
    @DisplayName("deve rejeitar email duplicado (UNIQUE constraint)")
    @Disabled("TODO 7: Implementar este teste")
    void shouldRejectDuplicateEmail() {
        // Arrange
        // TODO 7: Criar e salvar primeiro funcionário com saveAndFlush()

        // TODO 7: Criar segundo funcionário com MESMO email

        // Act & Assert
        // TODO 7: assertThatThrownBy para o segundo save
    }

    // ====================================================================
    // Testes de exemplo (já implementados como referência)
    // ====================================================================
    @Nested
    @DisplayName("Testes de referência")
    class Reference {

        @Test
        @DisplayName("deve retornar vazio para email inexistente")
        void shouldReturnEmptyForInvalidEmail() {
            Optional<Employee> found = employeeRepository.findByEmail("naoexiste@email.com");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("deve verificar existência por email")
        void shouldCheckExistsByEmail() {
            Employee emp = new Employee("Ana Silva", "ana.ref@email.com",
                    "123.456.789-09", new BigDecimal("5000.00"), department);
            employeeRepository.save(emp);

            assertThat(employeeRepository.existsByEmail("ana.ref@email.com")).isTrue();
            assertThat(employeeRepository.existsByEmail("naoexiste@email.com")).isFalse();
        }
    }
}
