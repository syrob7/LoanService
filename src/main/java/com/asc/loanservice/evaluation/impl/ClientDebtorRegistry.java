package com.asc.loanservice.evaluation.impl;

import com.asc.loanservice.contracts.CustomerCheckResultDto;
import com.asc.loanservice.contracts.LoanRequestEvaluationResult;
import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.evaluation.LoanEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class ClientDebtorRegistry implements LoanEvaluation {

    private static final String HTTP_CUSTOMERCHECK = "http://localhost:8090/api/customercheck/";
    private final RestTemplate restTemplate;

    @Override
    public void evaluate(Loan loan) {

        if(loan.getEvaluationResult() == LoanRequestEvaluationResult.REJECTED) return;

        CustomerCheckResultDto checkResultDto = restTemplate.getForObject(HTTP_CUSTOMERCHECK
                        + loan.getCustomerTaxId(), CustomerCheckResultDto.class);

        loan.setEvaluationResult(checkResultDto.isRegisteredDebtor ?
                LoanRequestEvaluationResult.REJECTED : LoanRequestEvaluationResult.APPROVED);

    }
}
