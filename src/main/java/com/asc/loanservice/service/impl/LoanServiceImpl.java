package com.asc.loanservice.service.impl;

import com.asc.loanservice.contracts.LoanRequestDataDto;
import com.asc.loanservice.contracts.LoanRequestDto;
import com.asc.loanservice.contracts.LoanRequestRegistrationResultDto;
import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.domain.LoanRepository;
import com.asc.loanservice.exceptions.LoanNotFoundException;
import com.asc.loanservice.service.EvaluationService;
import com.asc.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final ModelMapper modelMapper;
    private final EvaluationService evaluationService;

    @Override
    @Transactional
    public LoanRequestRegistrationResultDto save(LoanRequestDto loanRequest) {

        Loan loan = convertDtoToLoanEntity(loanRequest);
        setRegistrationDateAndLoanRequestNumber(loan);
        evaluationService.evaluateAll(loan);

        loanRepository.save(loan);
        return new LoanRequestRegistrationResultDto(loan.getLoanRequestNumber(),
                loan.getEvaluationResult());
    }

    @Override
    public LoanRequestDataDto getLoanByNumber(String loanNumber) {
        Loan loan = loanRepository
                .findLoanByLoanRequestNumber(loanNumber)
                .orElseThrow(() -> new LoanNotFoundException(loanNumber));

        return modelMapper.map(loan, LoanRequestDataDto.class);
    }

    private Loan convertDtoToLoanEntity(LoanRequestDto loanRequest) {
        Loan loan = modelMapper.map(loanRequest, Loan.class);

        return loan;
    }

    private void setRegistrationDateAndLoanRequestNumber(Loan loan) {
        Date date = new Date();
        loan.setRegistrationDate(LocalDateTime
                .ofInstant(date.toInstant(), ZoneId.systemDefault()));
        loan.setLoanRequestNumber(Long.toString(date.getTime()));
    }
}
