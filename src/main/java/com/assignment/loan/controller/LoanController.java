package com.assignment.loan.controller;

import com.assignment.loan.dtos.AggregateLoanDto;
import com.assignment.loan.dtos.LoanDto;
import com.assignment.loan.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/load/add")
    public ResponseEntity<LoanDto> addLoanData(@RequestBody @Valid LoanDto loanDto) {
        return ResponseEntity.ok(loanService.addLoanData(loanDto));
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/loans/{loanId}")
    public ResponseEntity<LoanDto> getLoanByLoanId(@PathVariable(value = "loanId") String loanId) {
        log.info("loanId {}", loanId);
        return ResponseEntity.ok(loanService.getLoanDtoByLoanId(loanId));
    }

    @GetMapping("/loans/customer/{customerId}")
    public ResponseEntity<List<LoanDto>> getLoanByCustomerId(@PathVariable(value = "customerId") String customerId) {
        return ResponseEntity.ok(loanService.getLoanDtoByCustomerId(customerId));
    }

    @GetMapping("/loans/lender/{lenderId}")
    public ResponseEntity<List<LoanDto>> getLoanByLenderId(@PathVariable(value = "lenderId") String lenderId) {
        return ResponseEntity.ok(loanService.getLoanDtoByLenderId(lenderId));
    }

    @GetMapping("/loans/aggregate/lender")
    public ResponseEntity<List<AggregateLoanDto>> getAggregateLoanByLenders() {
        return ResponseEntity.ok(loanService.getAggregateLoanByLenders());
    }

    @GetMapping("/loans/lender/customer")
    public ResponseEntity<List<AggregateLoanDto>> getAggregateLoanByCustomers() {
        return ResponseEntity.ok(loanService.getAggregateLoanByCustomers());
    }
}
