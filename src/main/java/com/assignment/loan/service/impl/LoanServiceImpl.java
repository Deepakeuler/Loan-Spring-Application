package com.assignment.loan.service.impl;

import com.assignment.loan.dtos.AggregateLoanDto;
import com.assignment.loan.dtos.LoanDto;
import com.assignment.loan.entity.LoanEntity;
import com.assignment.loan.repository.LoanDataRepository;
import com.assignment.loan.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoanDataRepository loanDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<LoanDto> getAllLoans() {
        return loanDataRepository.findAll().stream()
                .map(loanEntity -> objectMapper.convertValue(loanEntity, LoanDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public LoanDto addLoanData(LoanDto loanDto) {
        if(loanDto.getDueDate().isBefore(loanDto.getPaymentDate())) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(409), "Payment date should always be before the Due date");
        }
        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setLoanId(UUID.randomUUID().toString().replace("-", ""));
        loanEntity.setCustomerID(loanDto.getCustomerID());
        loanEntity.setLenderId(loanDto.getLenderId());
        loanEntity.setAmount(loanDto.getAmount());
        loanEntity.setRemainingAmount(loanDto.getRemainingAmount());
        loanEntity.setPaymentDate(loanDto.getPaymentDate());
        loanEntity.setInterestPerDay(loanDto.getInterestPerDay());
        loanEntity.setDueDate(loanDto.getDueDate());
        loanEntity.setPenaltyPerDay(loanDto.getPenaltyPerDay());
        loanEntity.setCancel(loanDto.getCancel());

        LoanEntity saveLoanEntity = loanDataRepository.save(loanEntity);
        return objectMapper.convertValue(saveLoanEntity, LoanDto.class);
    }

    @SneakyThrows
    @Override
    public LoanDto getLoanDtoByLoanId(String loanId) {
        LoanEntity loanEntity = loanDataRepository.getByLoanId(loanId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(409), "No loan data exists with loanId: " + loanId));
        if(loanEntity.getDueDate().isBefore(LocalDate.now())){
            log.warn("Loan's due date has passed, kindly check for loan Id: {}", loanEntity.getLoanId());
        }
        return objectMapper.convertValue(loanEntity, LoanDto.class);
    }

    @Override
    public List<LoanDto> getLoanDtoByCustomerId(String customerId) {
        List<LoanEntity> loanEntities = loanDataRepository.getByCustomerID(customerId);
        if(CollectionUtils.isEmpty(loanEntities)) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(409), "No loan data exists");
        }
        return loanEntities.stream()
                .map(loanEntity -> objectMapper.convertValue(loanEntity, LoanDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanDto> getLoanDtoByLenderId(String lenderId) {
        List<LoanEntity> loanEntities = loanDataRepository.getByLenderId(lenderId);
        if(CollectionUtils.isEmpty(loanEntities)) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(409), "No loan data exists");
        }
        return loanEntities.stream()
                .map(loanEntity -> objectMapper.convertValue(loanEntity, LoanDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AggregateLoanDto> getAggregateLoanByLenders() {
        List<LoanEntity> loanEntities = loanDataRepository.findAll();

        if(CollectionUtils.isEmpty(loanEntities)) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(409), "No loan data exists");
        }

        HashMap<String, AggregateLoanDto> lenders = new HashMap<>();
        loanEntities.forEach(loanEntity -> lenders.put(loanEntity.getLenderId(), new AggregateLoanDto(loanEntity.getLenderId(), null)));
        loanEntities.forEach(loanEntity -> {
            AggregateLoanDto aggregateLoanDto = lenders.get(loanEntity.getLenderId());
            aggregateLoanDto.setTotalRemainingAmount(aggregateLoanDto.getTotalRemainingAmount() + loanEntity.getRemainingAmount());
            aggregateLoanDto.setNetInterest(aggregateLoanDto.getNetInterest() + getInterestAmount(loanEntity));
            aggregateLoanDto.setNetPenalty(aggregateLoanDto.getNetPenalty() + getPenaltyAmount(loanEntity));
        });

        return lenders.values().stream().toList();
    }

    private Long getPenaltyAmount(LoanEntity loanEntity) {
        if(loanEntity.getDueDate().isAfter(LocalDate.now())) {
            return 0L;
        }
        long time = ChronoUnit.DAYS.between(loanEntity.getDueDate(), LocalDate.now()) - 1;
        return (long) (loanEntity.getRemainingAmount() * (Math.pow((1 + loanEntity.getPenaltyPerDay() / 100), time)) - loanEntity.getRemainingAmount());
    }

    private Long getInterestAmount(LoanEntity loanEntity) {
        long time = ChronoUnit.DAYS.between(loanEntity.getPaymentDate(), loanEntity.getDueDate()) - 1;
        return (long) (loanEntity.getRemainingAmount() * (Math.pow((1 + loanEntity.getInterestPerDay() / 100), time)) - loanEntity.getRemainingAmount());
    }

    @Override
    public List<AggregateLoanDto> getAggregateLoanByCustomers() {
        List<LoanEntity> loanEntities = loanDataRepository.findAll();
        if(CollectionUtils.isEmpty(loanEntities)) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(409), "No loan data exists");
        }
        HashMap<String, AggregateLoanDto> customers = new HashMap<>();
        loanEntities.forEach(loanEntity -> customers.put(loanEntity.getCustomerID(), new AggregateLoanDto(null, loanEntity.getCustomerID())));
        loanEntities.forEach(loanEntity -> {
            AggregateLoanDto aggregateLoanDto = customers.get(loanEntity.getCustomerID());
            aggregateLoanDto.setTotalRemainingAmount(aggregateLoanDto.getTotalRemainingAmount() + loanEntity.getRemainingAmount());
            aggregateLoanDto.setNetInterest(aggregateLoanDto.getNetInterest() + getInterestAmount(loanEntity));
            aggregateLoanDto.setNetPenalty(aggregateLoanDto.getNetPenalty() + getPenaltyAmount(loanEntity));
        });

        return customers.values().stream().toList();
    }
}
