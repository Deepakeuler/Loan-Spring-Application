package com.assignment.loan;

import com.assignment.loan.dtos.LoanDto;
import com.assignment.loan.entity.LoanEntity;
import com.assignment.loan.repository.LoanDataRepository;
import com.assignment.loan.service.impl.LoanServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoanApplicationTests {

	@Test
	void contextLoads() {
	}
	@Mock
	private LoanDataRepository loanDataRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	private LoanServiceImpl loanService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		loanService = new LoanServiceImpl();
		loanService.setLoanDataRepository(loanDataRepository);
		loanService.setObjectMapper(objectMapper);
	}

	@Test
	public void testGetAllLoans() {
		// Mock data
		LoanEntity loanEntity1 = new LoanEntity();
		loanEntity1.setLoanId(UUID.randomUUID().toString());
		LoanEntity loanEntity2 = new LoanEntity();
		loanEntity2.setLoanId(UUID.randomUUID().toString());

		when(loanDataRepository.findAll()).thenReturn(Arrays.asList(loanEntity1, loanEntity2));

		// Call the service method
		List<LoanDto> result = loanService.getAllLoans();

		// Assert the result
		assertEquals(2, ((List<?>) result).size());
	}

	@Test
	public void testAddLoanData() {
		// Mock input data
		LoanDto loanDto = new LoanDto();
		loanDto.setDueDate(LocalDate.now().plusDays(7)); // Payment date is before Due date

		// Mock behavior of the repository
		LoanEntity savedEntity = new LoanEntity();
		savedEntity.setLoanId(UUID.randomUUID().toString());

		when(loanDataRepository.save(ArgumentMatchers.any(LoanEntity.class))).thenReturn(savedEntity);

		// Call the service method
		assertThrows(HttpClientErrorException.class, () -> loanService.addLoanData(loanDto));
	}

    @Test
    public void testGetLoanDtoByLoanId() {
        // Mock data
        String loanId = UUID.randomUUID().toString();
        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setLoanId(loanId);
        loanEntity.setDueDate(LocalDate.now().plusDays(7)); // Future due date
        when(loanDataRepository.getByLoanId(loanId)).thenReturn(Optional.of(loanEntity));

        LoanDto result = loanService.getLoanDtoByLoanId(loanId);

        assertEquals(loanId, result.getLoanId());
    }

    @Test
    public void testGetLoanDtoByLoanIdDueDatePassed() {
        // Mock data with past due date
        String loanId = UUID.randomUUID().toString();
        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setLoanId(loanId);
        loanEntity.setDueDate(LocalDate.now().minusDays(1)); // Past due date
        when(loanDataRepository.getByLoanId(loanId)).thenReturn(Optional.of(loanEntity));

        assertDoesNotThrow(() -> loanService.getLoanDtoByLoanId(loanId));
    }


}
