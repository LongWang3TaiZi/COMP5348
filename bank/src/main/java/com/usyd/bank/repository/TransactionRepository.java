package com.usyd.bank.repository;

import com.usyd.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByFromAccountEmail(String fromAccountEmail);
    Transaction findByOriginalTransactionId(Long transactionId);
}
