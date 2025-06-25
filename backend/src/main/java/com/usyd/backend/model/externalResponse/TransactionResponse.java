package com.usyd.backend.model.externalResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String transactionType;
    private String transactionStatus;
    private Long originalTransactionId;
}