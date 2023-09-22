package com.assignment.loan.repository;

import com.assignment.loan.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanDataRepository extends JpaRepository<LoanEntity, Long> {
    Optional<LoanEntity> getByLoanId(String loanId);

    List<LoanEntity> getByCustomerID(String customerId);

    List<LoanEntity> getByLenderId(String lenderId);
}
