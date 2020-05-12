package com.asc.loanservice.service;

import com.asc.loanservice.contracts.LoanRequestDataDto;
import com.asc.loanservice.contracts.LoanRequestDto;
import com.asc.loanservice.contracts.LoanRequestRegistrationResultDto;

public interface LoanService {
    LoanRequestRegistrationResultDto save(LoanRequestDto loanRequest);
    LoanRequestDataDto getLoanByNumber(String loanNumber);
}
