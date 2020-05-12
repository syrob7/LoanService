package com.asc.loanservice;

import com.asc.loanservice.contracts.*;
import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.domain.LoanRepository;
import com.asc.loanservice.evaluation.LoanEvaluation;
import com.asc.loanservice.evaluation.impl.ClientAgeOnLastInstallmentDate;
import com.asc.loanservice.evaluation.impl.ClientDebtorRegistry;
import com.asc.loanservice.evaluation.impl.InstallmentSalaryClientLimit;
import com.asc.loanservice.exceptions.LoanNotFoundException;
import com.asc.loanservice.service.EvaluationService;
import com.asc.loanservice.service.LoanService;
import com.asc.loanservice.service.impl.EvaluationServiceImpl;
import com.asc.loanservice.service.impl.LoanServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoanServiceTest {

    private static final String URI = "http://localhost:8090/api/customercheck/";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private  LoanRepository loanRepository;

    @InjectMocks
    private ModelMapper modelMapper;

    private LoanService loanService;

    @Before
    public void setUp() {
        EvaluationService evaluationService = new EvaluationServiceImpl(prepareLoanEvaluationList());
        loanService = new LoanServiceImpl(loanRepository, modelMapper, evaluationService);
    }

    @Test
    public void testSaveLoanWithApprovedResult() {
        //given
        LoanRequestDto loanRequestDto = prepareApprovedLoanRequestDto();
        CustomerCheckResultDto checkResultDto = prepareCustomerCheckResultDto();

        Mockito.doReturn(checkResultDto)
                .when(restTemplate)
                .getForObject(URI + loanRequestDto.getCustomerTaxId(), CustomerCheckResultDto.class);

        Mockito.doReturn(new Loan())
                .when(loanRepository)
                .save(isA(Loan.class));

        //when
        LoanRequestRegistrationResultDto resultDto = loanService.save(loanRequestDto);

        //then
        assertThat(resultDto.getEvaluationResult(), equalTo(LoanRequestEvaluationResult.APPROVED));
    }

    @Test
    public void testSaveLoanWithRejectedResult() {
        //given
        LoanRequestDto loanRequestDto = prepareRejectedLoanRequestDto();
        CustomerCheckResultDto checkResultDto = prepareCustomerCheckResultDto();

        Mockito.doReturn(checkResultDto)
                .when(restTemplate)
                .getForObject(URI + loanRequestDto.getCustomerTaxId(), CustomerCheckResultDto.class);

        Mockito.doReturn(new Loan())
                .when(loanRepository)
                .save(isA(Loan.class));

        //when
        LoanRequestRegistrationResultDto resultDto = loanService.save(loanRequestDto);

        //then
        assertThat(resultDto.getEvaluationResult(), equalTo(LoanRequestEvaluationResult.REJECTED));
    }

    @Test
    public void testGetLoanByNumber() {
        //given
        Loan loan = prepareLoan();
        Mockito.when(loanRepository.findLoanByLoanRequestNumber("123456"))
                .thenReturn(java.util.Optional.of(loan));

        //when
        LoanRequestDataDto loanByNumber = loanService.getLoanByNumber("123456");

        //then
        assertThat(loanByNumber.getCustomerTaxId(), equalTo("123-456"));
        assertThat(loanByNumber.getEvaluationResult(), equalTo(LoanRequestEvaluationResult.APPROVED));
        assertThat(loanByNumber.getNumberOfInstallments(), equalTo(12));
        assertThat(loanByNumber.getLoanAmount(), equalTo(BigDecimal.valueOf(12000)));

        verify(loanRepository, times(1)).findLoanByLoanRequestNumber(any(String.class));
    }

    @Test(expected = LoanNotFoundException.class)
    public void testGetLoanByNumberLoanNotFoundException() {
        Mockito.when(loanRepository.findLoanByLoanRequestNumber("123456"))
                .thenThrow(LoanNotFoundException.class);

        //when
        LoanRequestDataDto loanByNumber = loanService.getLoanByNumber("123456");
    }

    private  Loan prepareLoan() {
        Loan loan = new Loan();
        loan.setLoanRequestNumber("1234567");
        loan.setCustomerName("Kowalski");
        loan.setCustomerBirthday(LocalDate.of(1990, 5, 7));
        loan.setCustomerTaxId("123-456");
        loan.setCustomerMonthlyIncome(BigDecimal.valueOf(10000));
        loan.setLoanAmount(BigDecimal.valueOf(12000));
        loan.setNumberOfInstallments(12);
        loan.setFirstInstallmentDate(LocalDate.of(2020, 6, 7));
        loan.setEvaluationResult(LoanRequestEvaluationResult.APPROVED);
        loan.setRegistrationDate(LocalDateTime.of(2020, 5, 1, 10, 0));

        return loan;
    }

    private LoanRequestDto prepareApprovedLoanRequestDto() {
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

    private LoanRequestDto prepareRejectedLoanRequestDto() {
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setCustomerBirthday(LocalDate.of(1990, 5, 7));
        loanRequestDto.setCustomerMonthlyIncome(BigDecimal.valueOf(1000));
        loanRequestDto.setCustomerName("Kowalski");
        loanRequestDto.setCustomerTaxId("123456");
        loanRequestDto.setFirstInstallmentDate(LocalDate.of(2020, 6, 1));
        loanRequestDto.setLoanAmount(BigDecimal.valueOf(12000));
        loanRequestDto.setNumberOfInstallments(12);

        return loanRequestDto;
    }

    private  CustomerCheckResultDto prepareCustomerCheckResultDto() {
        CustomerCheckResultDto checkResultDto = new CustomerCheckResultDto();
        checkResultDto.customerTaxId = "123456";
        checkResultDto.isRegisteredDebtor = Boolean.FALSE;

        return checkResultDto;
    }

    private List<LoanEvaluation> prepareLoanEvaluationList() {
        LoanEvaluation clientDebtorRegistry = new ClientDebtorRegistry(restTemplate);
        LoanEvaluation clientAge = new ClientAgeOnLastInstallmentDate();
        LoanEvaluation installmentSalaryClientLimit = new InstallmentSalaryClientLimit();

        return Stream
                .of(
                    clientDebtorRegistry,
                    clientAge,
                    installmentSalaryClientLimit)
                .collect(Collectors.toList());
    }
}
