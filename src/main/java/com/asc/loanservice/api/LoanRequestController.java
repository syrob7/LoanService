package com.asc.loanservice.api;

import com.asc.loanservice.contracts.LoanRequestDataDto;
import com.asc.loanservice.contracts.LoanRequestDto;
import com.asc.loanservice.contracts.LoanRequestRegistrationResultDto;
import com.asc.loanservice.domain.Loan;
import com.asc.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/api/loans")
public class LoanRequestController {

    private final LoanService loanService;

    @PostMapping
    public LoanRequestRegistrationResultDto register(@Valid @RequestBody LoanRequestDto loanRequest){
        return loanService.save(loanRequest);
    }

    @GetMapping("/{loanNumber}")
    public LoanRequestDataDto getByNumber(@PathVariable("loanNumber") String loanNumber){
        return loanService.getLoanByNumber(loanNumber);
    }
}
