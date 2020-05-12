package com.asc.loanservice;

import com.asc.loanservice.api.LoanRequestController;
import com.asc.loanservice.contracts.LoanRequestDataDto;
import com.asc.loanservice.contracts.LoanRequestDto;
import com.asc.loanservice.contracts.LoanRequestEvaluationResult;
import com.asc.loanservice.contracts.LoanRequestRegistrationResultDto;
import com.asc.loanservice.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
public class LoanApplicationTests {

	private MockMvc mockMvc;

	@InjectMocks
	private LoanRequestController loanRequestController;

	@Mock
	private LoanService loanService;

	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(loanRequestController).build();
	}

	@Test
	public void shouldReturnLoan() throws Exception {
	//given
	LoanRequestDataDto loanRequestDataDto = prepareLoanRequestDataDto();
	when(loanService.getLoanByNumber("12345678")).thenReturn(loanRequestDataDto);

	//when
	mockMvc.perform(get("/api/loans/12345678"))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.loanRequestNumber").value("12345678"))
			.andExpect(jsonPath("$.customerName").value("Kowalski"))
			.andExpect(jsonPath("$.customerBirthday[0]").value("1990"))
			.andExpect(jsonPath("$.customerBirthday[1]").value("5"))
			.andExpect(jsonPath("$.customerBirthday[2]").value("7"))
			.andExpect(jsonPath("$.customerMonthlyIncome").value(BigDecimal.valueOf(10000)))
			.andExpect(jsonPath("$.loanAmount").value(BigDecimal.valueOf(12000)))
			.andExpect(jsonPath("$.numberOfInstallments").value(12))
			.andExpect(jsonPath("$.firstInstallmentDate[0]").value("2020"))
			.andExpect(jsonPath("$.firstInstallmentDate[1]").value("6"))
			.andExpect(jsonPath("$.firstInstallmentDate[2]").value("7"))
			.andExpect(jsonPath("$.evaluationResult").value(LoanRequestEvaluationResult.APPROVED.toString()))
			.andExpect(jsonPath("$.registrationDate[0]").value("2020"))
			.andExpect(jsonPath("$.registrationDate[1]").value("5"))
			.andExpect(jsonPath("$.registrationDate[2]").value("1"))
			.andExpect(jsonPath("$.registrationDate[3]").value("10"))
			.andExpect(jsonPath("$.registrationDate[4]").value("0"))
			.andExpect(jsonPath("$.customerTaxId").value("123-456"));

		verify(loanService, times(1)).getLoanByNumber("12345678");
	}

	@Test
	public void shouldSaveLoan() throws Exception {
		//given
		LoanRequestDto loanRequestDto = prepareLoanRequestDto();
		LoanRequestRegistrationResultDto registrationResultDto = prepareLoanRequestRegistrationResultDto();
		when(loanService.save(any(LoanRequestDto.class))).thenReturn(registrationResultDto);

		//when
		mockMvc.perform(post("/api/loans")
					.contentType(APPLICATION_JSON_UTF8)
					.content(convertObjectToJsonBytes(loanRequestDto)))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.loanRequestNumber").value("987654321"))
				.andExpect(jsonPath("$.evaluationResult").value(LoanRequestEvaluationResult.APPROVED.toString()));

		//then
		verify(loanService, times(1)).save(any(LoanRequestDto.class));
	}

	private LoanRequestDto prepareLoanRequestDto() {
		LoanRequestDto loanRequestDto = new LoanRequestDto();
		loanRequestDto.setCustomerBirthday(LocalDate.of(1990, 5, 7));
		loanRequestDto.setCustomerMonthlyIncome(BigDecimal.valueOf(10000));
		loanRequestDto.setCustomerName("Kowalski");
		loanRequestDto.setCustomerTaxId("123456");
		loanRequestDto.setFirstInstallmentDate(LocalDate.of(2020, 6, 1));
		loanRequestDto.setLoanAmount(BigDecimal.valueOf(12000));
		loanRequestDto.setNumberOfInstallments(12);

		return loanRequestDto;
	}

	private LoanRequestDataDto prepareLoanRequestDataDto() {
		LoanRequestDataDto loanRequestDataDto = new LoanRequestDataDto();
		loanRequestDataDto.setLoanRequestNumber("12345678");
		loanRequestDataDto.setCustomerBirthday(LocalDate.of(1990, 5, 7));
		loanRequestDataDto.setCustomerMonthlyIncome(BigDecimal.valueOf(10000));
		loanRequestDataDto.setCustomerName("Kowalski");
		loanRequestDataDto.setCustomerTaxId("123-456");
		loanRequestDataDto.setEvaluationResult(LoanRequestEvaluationResult.APPROVED);
		loanRequestDataDto.setFirstInstallmentDate(LocalDate.of(2020, 6, 7));
		loanRequestDataDto.setNumberOfInstallments(12);
		loanRequestDataDto.setRegistrationDate(LocalDateTime.of(2020, 5, 1, 10, 0));
		loanRequestDataDto.setLoanAmount(BigDecimal.valueOf(12000));

		return loanRequestDataDto;
	}

	private LoanRequestRegistrationResultDto prepareLoanRequestRegistrationResultDto() {
		LoanRequestRegistrationResultDto registrationResultDto = new LoanRequestRegistrationResultDto();
		registrationResultDto.setEvaluationResult(LoanRequestEvaluationResult.APPROVED);
		registrationResultDto.setLoanRequestNumber("987654321");

		return registrationResultDto;
	}

	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		return new ObjectMapper().writeValueAsBytes(object);
	}
}
