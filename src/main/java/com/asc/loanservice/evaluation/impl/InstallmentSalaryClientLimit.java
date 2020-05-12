package com.asc.loanservice.evaluation.impl;

import com.asc.loanservice.contracts.LoanRequestEvaluationResult;
import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.evaluation.LoanEvaluation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InstallmentSalaryClientLimit implements LoanEvaluation {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    public static final BigDecimal PERCENTAGE = new BigDecimal(15);
    public static final BigDecimal LOAN_RATE_PER_YEAR = new BigDecimal("0.04");

    @Override
    public void evaluate(Loan loan) {

        if(loan.getEvaluationResult() == LoanRequestEvaluationResult.REJECTED) return;

        BigDecimal _15_PctMonthlyCustomerIncome = loan.getCustomerMonthlyIncome().multiply(PERCENTAGE).divide(ONE_HUNDRED);
        BigDecimal totalLoanInstallment = calculateTotalLoanInstallment(loan);

        loan.setEvaluationResult(totalLoanInstallment.compareTo(_15_PctMonthlyCustomerIncome) == 1 ?
                LoanRequestEvaluationResult.REJECTED : LoanRequestEvaluationResult.APPROVED);
    }

    private BigDecimal calculateTotalLoanInstallment(Loan loan) {
        BigDecimal installmentAmountPerMonth = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(loan.getNumberOfInstallments()));

        BigDecimal interestAmountPerMonth = loan.getLoanAmount()
                .multiply(LOAN_RATE_PER_YEAR)
                .multiply(BigDecimal.valueOf(31))
                .divide(BigDecimal.valueOf(365), BigDecimal.ROUND_HALF_UP);

        return installmentAmountPerMonth.add(interestAmountPerMonth);
    }
}
