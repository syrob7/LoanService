package com.asc.loanservice.evaluation.impl;

import com.asc.loanservice.contracts.LoanRequestEvaluationResult;
import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.evaluation.LoanEvaluation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class ClientAgeOnLastInstallmentDate implements LoanEvaluation {

    public static final int MAX_CUSTOMER_AGE = 65;

    @Override
    public void evaluate(Loan loan) {

        if(loan.getEvaluationResult() == LoanRequestEvaluationResult.REJECTED) return;

        LocalDate installmentsEndDate = loan.getFirstInstallmentDate().plusMonths(loan.getNumberOfInstallments());
        int customerAge = Period.between(loan.getCustomerBirthday(), installmentsEndDate).getYears();

        loan.setEvaluationResult(customerAge > MAX_CUSTOMER_AGE ? LoanRequestEvaluationResult.REJECTED
                : LoanRequestEvaluationResult.APPROVED);
    }
}
