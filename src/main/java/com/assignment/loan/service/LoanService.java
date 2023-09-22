package com.assignment.loan.service;

import com.assignment.loan.dtos.AggregateLoanDto;
import com.assignment.loan.dtos.LoanDto;

import java.util.List;

public interface LoanService {

    List<LoanDto> getAllLoans();

    LoanDto addLoanData(LoanDto loanDto);
    LoanDto getLoanDtoByLoanId(String loanId);

    List<LoanDto> getLoanDtoByCustomerId(String customerId);
    List<LoanDto> getLoanDtoByLenderId(String lenderId);

    List<AggregateLoanDto> getAggregateLoanByLenders();

    List<AggregateLoanDto> getAggregateLoanByCustomers();
}
