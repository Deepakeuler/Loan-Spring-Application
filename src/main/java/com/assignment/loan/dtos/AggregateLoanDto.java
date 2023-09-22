package com.assignment.loan.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregateLoanDto {
    private String lenderId;
    private String customerId;
    private Long totalRemainingAmount = 0L;
    private Long netInterest = 0L;
    private Long netPenalty = 0L;

    public AggregateLoanDto(String lenderId, String customerId){
        this.lenderId = lenderId;
        this.customerId = customerId;
    }
}
