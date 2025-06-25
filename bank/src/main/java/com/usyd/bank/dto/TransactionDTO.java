package com.usyd.bank.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.bank.model.Transaction;
import com.usyd.bank.util.TransactionStatus;
import com.usyd.bank.util.TransactionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionDTO {
    private long transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private long originalTransactionId;

    public TransactionDTO(Transaction transactionEntity){
        this.transactionId = transactionEntity.getId();
        this.fromAccount = transactionEntity.getFromAccount();
        this.toAccount = transactionEntity.getToAccount();
        this.amount = transactionEntity.getAmount();
        this.transactionType = transactionEntity.getTransactionType();
        this.transactionStatus = transactionEntity.getStatus();
        this.originalTransactionId = transactionEntity.getOriginalTransactionId();
    }
    public TransactionDTO(Transaction transactionEntity, long originalTransactionId){
        this.transactionId = transactionEntity.getId();
        this.fromAccount = transactionEntity.getFromAccount();
        this.toAccount = transactionEntity.getToAccount();
        this.amount = transactionEntity.getAmount();
        this.transactionType = transactionEntity.getTransactionType();
        this.transactionStatus = transactionEntity.getStatus();
        this.originalTransactionId = originalTransactionId;
    }
}
