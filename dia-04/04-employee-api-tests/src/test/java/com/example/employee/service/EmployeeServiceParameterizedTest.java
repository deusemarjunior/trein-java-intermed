package com.example.employee.service;

import com.example.employee.builder.EmployeeBuilder;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Testes parametrizados do {@link EmployeeService}.
 *
 * <p>Exercício TODO 5 — implemente os testes parametrizados.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService — Testes Parametrizados")
class EmployeeServiceParameterizedTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService service;

    // ====================================================================
    // TODO 5: Teste parametrizado de validação de CPF
    // ====================================================================

    /**
     * TODO 5: Implementar teste parametrizado para CPFs válidos
     *
     * Use @ParameterizedTest com @CsvSource para testar múltiplos CPFs.
     * Cada linha deve ter: cpf, nome, email
     *
     * CPFs válidos para testar:
     *   "123.456.789-09, Ana Silva, ana@email.com"
     *   "987.654.321-00, Carlos Santos, carlos@email.com"
     *   "111.222.333-44, Maria Oliveira, maria@email.com"
     *
     * Passos:
     * 1. Criar request com os dados da linha @CsvSource
     * 2. Configurar mocks necessários
     * 3. Chamar service.create(request)
     * 4. Verificar que o CPF no resultado é o esperado
     */
    @ParameterizedTest(name = "CPF {0} para {1}")
    @DisplayName("deve aceitar CPFs no formato válido")
    @CsvSource({
            "123.456.789-09, Ana Silva, ana@email.com",
            "987.654.321-00, Carlos Santos, carlos@email.com",
            "111.222.333-44, Maria Oliveira, maria@email.com"
    })
    @Disabled("TODO 5: Implementar este teste")
    void shouldAcceptValidCpfFormats(String cpf, String name, String email) {
        // Arrange
        // TODO 5: Criar request com EmployeeBuilder usando cpf, name, email

        // TODO 5: Configurar mocks (existsByEmail → false, findById departamento, save)

        // Act
        // TODO 5: Chamar service.create(request)

        // Assert
        // TODO 5: Verificar que result.cpf() == cpf
    }

    /**
     * Teste parametrizado de salários inválidos (exemplo — já implementado).
     */
    @ParameterizedTest(name = "salário R$ {0} deve ser rejeitado")
    @DisplayName("deve rejeitar salários abaixo do mínimo")
    @ValueSource(strings = {"0.01", "500.00", "1000.00", "1411.99"})
    void shouldRejectInvalidSalaries(String salaryStr) {
        BigDecimal invalidSalary = new BigDecimal(salaryStr);

        // Nota: como o EmployeeBuilder ainda não está implementado,
        // este teste só funciona após TODO 1 ser concluído.
        // Quando implementado, use:
        //   var request = new EmployeeBuilder()
        //       .withSalary(invalidSalary)
        //       .withEmail("test-" + salaryStr + "@email.com")
        //       .buildRequest();
        //   assertThatThrownBy(() -> service.create(request))
        //       .isInstanceOf(SalaryBelowMinimumException.class);
    }
}
