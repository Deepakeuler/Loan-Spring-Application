package com.assignment.loan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class LoanEntity extends BaseEntity {
    @Column(name = "loan_id", unique = true, nullable = true)
    private String loanId;

    @Column(name = "customer_id")
    private String customerID;

    @Column(name = "lender_id")
    private String lenderId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "remaining_amount")
    private Long remainingAmount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "interest_per_day")
    private Double interestPerDay;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "penalty_per_day")
    private Double penaltyPerDay;

    @Column(name = "cancel")
    private Boolean cancel;
}
