package com.asc.loanservice.exceptions;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(String loanNumber) {
        super("Could not find loan: " + loanNumber);
    }
}
