package com.example.employee.service;

import com.example.employee.builder.EmployeeBuilder;
import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.exception.SalaryBelowMinimumException;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do {@link EmployeeService} usando Mockito.
 *
 * <p>Exercícios TODO 2, 3 e 4 — implemente os testes marcados abaixo.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService — Testes Unitários")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService service;

    @Captor
    private ArgumentCaptor<Employee> employeeCaptor;

    // ====================================================================
    // TODO 2: Teste de criação com sucesso
    // ====================================================================
    @Nested
    @DisplayName("create()")
    class Create {

        /**
         * TODO 2: Implementar teste de criação com sucesso
         *
         * Passos:
         * 1. Criar um EmployeeRequest usando o EmployeeBuilder
         * 2. Configurar mocks:
         *    - employeeRepository.existsByEmail(...) → false
         *    - departmentRepository.findById(...) → Optional.of(department)
         *    - employeeRepository.save(...) → employee salvo
         * 3. Chamar service.create(request)
         * 4. Verificar que o resultado tem os dados corretos
         * 5. Usar ArgumentCaptor para verificar o que foi salvo
         */
        @Test
        @DisplayName("deve criar funcionário com sucesso")
        @Disabled("TODO 2: Implementar este teste")
        void shouldCreateEmployee() {
            // Arrange
            // TODO 2: Criar request com EmployeeBuilder
            // TODO 2: Configurar mocks (when/thenReturn)

            // Act
            // TODO 2: Chamar service.create(request)

            // Assert
            // TODO 2: Verificar resultado com assertThat
            // TODO 2: Usar verify + ArgumentCaptor
        }

        // ================================================================
        // TODO 3: Teste de salário mínimo
        // ================================================================

        /**
         * TODO 3: Implementar teste que verifica a regra de salário mínimo
         *
         * Passos:
         * 1. Criar request com salário abaixo de R$ 1.412,00
         * 2. Chamar service.create(request)
         * 3. Verificar que lança SalaryBelowMinimumException
         * 4. Verificar que save() NUNCA foi chamado
         */
        @Test
        @DisplayName("deve lançar exceção quando salário é menor que o mínimo")
        @Disabled("TODO 3: Implementar este teste")
        void shouldThrowWhenSalaryBelowMinimum() {
            // Arrange
            // TODO 3: Criar request com salário = 1000.00

            // Act & Assert
            // TODO 3: assertThatThrownBy + isInstanceOf(SalaryBelowMinimumException.class)
            // TODO 3: verify(employeeRepository, never()).save(any())
        }

        // ================================================================
        // TODO 4: Teste de email duplicado
        // ================================================================

        /**
         * TODO 4: Implementar teste que verifica a regra de email único
         *
         * Passos:
         * 1. Criar request com email que já existe
         * 2. Configurar mock: existsByEmail → true
         * 3. Chamar service.create(request)
         * 4. Verificar que lança DuplicateEmailException
         * 5. Verificar que save() NUNCA foi chamado
         */
        @Test
        @DisplayName("deve lançar exceção quando email já existe")
        @Disabled("TODO 4: Implementar este teste")
        void shouldThrowWhenEmailExists() {
            // Arrange
            // TODO 4: Criar request com EmployeeBuilder

            // TODO 4: Configurar mock existsByEmail → true

            // Act & Assert
            // TODO 4: assertThatThrownBy + isInstanceOf(DuplicateEmailException.class)
            // TODO 4: verify(employeeRepository, never()).save(any())
        }
    }

    // ====================================================================
    // findById — Testes de exemplo (já implementados)
    // ====================================================================
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("deve lançar exceção quando funcionário não encontrado")
        void shouldThrowWhenNotFound() {
            when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(999L))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    // ====================================================================
    // delete — Testes de exemplo (já implementados)
    // ====================================================================
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve deletar funcionário existente")
        void shouldDeleteEmployee() {
            when(employeeRepository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(employeeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar exceção ao deletar inexistente")
        void shouldThrowWhenDeleting() {
            when(employeeRepository.existsById(anyLong())).thenReturn(false);

            assertThatThrownBy(() -> service.delete(999L))
                    .isInstanceOf(EmployeeNotFoundException.class);

            verify(employeeRepository, never()).deleteById(anyLong());
        }
    }
}
