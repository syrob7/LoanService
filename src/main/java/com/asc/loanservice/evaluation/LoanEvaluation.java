package com.asc.loanservice.evaluation;

import com.asc.loanservice.domain.Loan;

public interface LoanEvaluation {

    void evaluate(Loan loan);
}
