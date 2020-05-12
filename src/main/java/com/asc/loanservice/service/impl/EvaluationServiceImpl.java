package com.asc.loanservice.service.impl;

import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.evaluation.LoanEvaluation;
import com.asc.loanservice.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final List<LoanEvaluation> loanEvaluationList;

    @Override
    public void evaluateAll(Loan loan) {
        loanEvaluationList
                .stream()
                .forEach(e -> e.evaluate(loan));
    }
}
