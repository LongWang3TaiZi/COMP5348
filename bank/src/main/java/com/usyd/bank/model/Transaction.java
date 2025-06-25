package com.usyd.bank.model;


import com.usyd.bank.model.request.CreateTransactionRequest;
import com.usyd.bank.util.TransactionStatus;
import com.usyd.bank.util.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String fromAccount;
    @Column(nullable = false)
    private String toAccount;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(nullable = true)
    private String fromAccountEmail;

    private long originalTransactionId;

    private LocalDateTime modifyTime;

    public Transaction(CreateTransactionRequest transactionEntity, TransactionStatus status,
                       TransactionType transactionType, String fromAccountEmail){
        this.fromAccount = transactionEntity.getFromAccount();
        this.toAccount = transactionEntity.getToAccount();
        this.amount = transactionEntity.getAmount();
        this.transactionType = transactionType;
        this.status = status;
        this.fromAccountEmail = fromAccountEmail;
    }

    public Transaction(String fromAccount, String toAccount, Double amount, TransactionStatus status,
                       TransactionType transactionType, long originalTransactionId, String fromAccountEmail){
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.originalTransactionId = originalTransactionId;
        this.fromAccountEmail = fromAccountEmail;
    }
}
