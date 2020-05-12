package com.asc.loanservice.contracts;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {

    @NotEmpty
    private String customerName;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    @Past
    private LocalDate customerBirthday;

    @NotEmpty
    private String customerTaxId;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal customerMonthlyIncome;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal loanAmount;

    @NotNull
    @Min(1)
    private Integer numberOfInstallments;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    @Future
    private LocalDate firstInstallmentDate;
}
