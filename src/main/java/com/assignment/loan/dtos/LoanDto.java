package com.assignment.loan.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
    private String loanId;

    @NotNull
    private String customerID;

    @NotNull
    private String lenderId;

    @NotNull
    private Long amount;

    @NotNull
    private Long remainingAmount;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private Double interestPerDay;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private Double penaltyPerDay;

    @NotNull
    private Boolean cancel = false;
}
