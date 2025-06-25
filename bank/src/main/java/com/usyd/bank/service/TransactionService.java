package com.usyd.bank.service;

import com.usyd.bank.dto.TransactionDTO;
import com.usyd.bank.model.Account;
import com.usyd.bank.model.Transaction;
import com.usyd.bank.model.request.CreateTransactionRequest;
import com.usyd.bank.repository.AccountRepository;
import com.usyd.bank.repository.TransactionRepository;
import com.usyd.bank.util.TransactionStatus;
import com.usyd.bank.util.TransactionType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionDTO createPayment(CreateTransactionRequest request) {
        // find source and destination accounts
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccount());
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccount());
        // verify both accounts exist
        if (fromAccount != null && toAccount != null) {
            // check if source account has sufficient balance
            if (request.getAmount() > fromAccount.getBalance()) {
                // create a failed transaction record due to insufficient balance
                Transaction transaction = new Transaction(request, TransactionStatus.InsufficientBalance,
                        TransactionType.Payment, fromAccount.getEmail());
                transaction.setModifyTime(LocalDateTime.now());
                Transaction savedTransaction = transactionRepository.save(transaction);
                return new TransactionDTO(savedTransaction);
            } else {
                // update balances for both accounts
                fromAccount.updateBalance(-request.getAmount());
                toAccount.updateBalance(request.getAmount());
                fromAccount.setModifyTime(LocalDateTime.now());
                toAccount.setModifyTime(LocalDateTime.now());

                // save the updated account balances
                Account updatedFromAccount = accountRepository.save(fromAccount);
                Account updatedToAccount = accountRepository.save(toAccount);
                // verify account updates were successful
                if (updatedFromAccount != null && updatedToAccount != null) {
                    // create a successful transaction record
                    Transaction transaction = new Transaction(request, TransactionStatus.Accepted,
                            TransactionType.Payment, fromAccount.getEmail());
                    transaction.setModifyTime(LocalDateTime.now());
                    Transaction savedTransaction = transactionRepository.save(transaction);
                    return new TransactionDTO(savedTransaction);
                } else {
                    // create a failed transaction record if account updates failed
                    Transaction transaction = new Transaction(request, TransactionStatus.Failed,
                            TransactionType.Payment, fromAccount.getEmail());
                    transaction.setModifyTime(LocalDateTime.now());
                    Transaction savedTransaction = transactionRepository.save(transaction);
                    return new TransactionDTO(savedTransaction);
                }
            }
        } else {
            // create a failed transaction record if either account not found
            Transaction transaction = new Transaction(request, TransactionStatus.Failed, TransactionType.Payment,
                    fromAccount.getEmail());
            transaction.setModifyTime(LocalDateTime.now());
            Transaction savedTransaction = transactionRepository.save(transaction);
            return new TransactionDTO(savedTransaction);
        }
    }

    @Transactional
    public TransactionDTO refundTransaction(String transactionId) {
        // find the original transaction
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);

        // check if refund already exists for this transaction
        Transaction existsRefundTransaction = transactionRepository.findByOriginalTransactionId(Long.parseLong(transactionId));
        if (existsRefundTransaction != null) return null;

        if (transaction.isPresent()) {
            // verify it's a payment transaction (only payments can be refunded)
            if (transaction.get().getTransactionType() == TransactionType.Payment) {
                // find both accounts involved in the original transaction
                Account fromAccount = accountRepository.findByAccountNumber(transaction.get()
                        .getFromAccount());
                Account toAccount = accountRepository.findByAccountNumber(transaction.get()
                        .getToAccount());
                // verify both accounts still exist
                if (fromAccount != null && toAccount != null) {
                    // reverse the original transaction amounts
                    fromAccount.updateBalance(transaction.get().getAmount());
                    toAccount.updateBalance(-transaction.get().getAmount());
                    fromAccount.setModifyTime(LocalDateTime.now());
                    toAccount.setModifyTime(LocalDateTime.now());

                    // save the updated account balances
                    Account updatedFromAccount = accountRepository.save(fromAccount);
                    Account updatedToAccount = accountRepository.save(toAccount);

                    // verify account updates were successful
                    if (updatedFromAccount != null && updatedToAccount != null) {
                        // create refund transaction record with reference to original transaction
                        Transaction originalTransaction = transaction.get();
                        Transaction refundTransaction = new Transaction(originalTransaction.getFromAccount(),
                                originalTransaction.getToAccount(), originalTransaction.getAmount(),
                                TransactionStatus.Accepted, TransactionType.Refund, originalTransaction.getId(),
                                originalTransaction.getFromAccountEmail());
                        refundTransaction.setModifyTime(LocalDateTime.now());
                        Transaction savedRefundTransaction = transactionRepository.save(refundTransaction);
                        return new TransactionDTO(savedRefundTransaction);
                    }
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean chargeBackTransaction(String refundId){
        Optional<Transaction> transactionOpt = transactionRepository.findById(refundId);
        if (!transactionOpt.isPresent()) {
            logger.warn("Refund transaction not found for id: {}", refundId);
            return false;
        }

        Transaction refundTransaction = transactionOpt.get();
        if (refundTransaction.getTransactionType() != TransactionType.Refund) {
            logger.warn("Transaction {} is not a refund transaction", refundId);
            return false;
        }

        // check if chargeback already exists
        Transaction existingChargeback = transactionRepository.findByOriginalTransactionId(Long.parseLong(refundId));
        if (existingChargeback != null) {
            logger.warn("Chargeback already exists for refund transaction: {}", refundId);
            return false;
        }

        Account fromAccount = accountRepository.findByAccountNumber(refundTransaction.getFromAccount());
        Account toAccount = accountRepository.findByAccountNumber(refundTransaction.getToAccount());

        if (fromAccount == null || toAccount == null) {
            logger.error("Unable to find accounts for refund transaction: {}", refundId);
            return false;
        }

        // update account balances (reverse of refund)
        fromAccount.updateBalance(-refundTransaction.getAmount());
        toAccount.updateBalance(refundTransaction.getAmount());
        fromAccount.setModifyTime(LocalDateTime.now());
        toAccount.setModifyTime(LocalDateTime.now());
        Account updatedFromAccount = accountRepository.save(fromAccount);
        Account updatedToAccount = accountRepository.save(toAccount);

        if (updatedFromAccount == null || updatedToAccount == null) {
            logger.error("failed to update account balances for chargeback of refund: {}", refundId);
            throw new RuntimeException("Failed to update account balances");
        }

        transactionRepository.deleteById(refundId);
        return true;
    }

    @Transactional
    public TransactionDTO getTransaction(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isPresent()) {
            return new TransactionDTO(transaction.get());
        }
        return null;
    }

    @Transactional
    public List<TransactionDTO> getTransactionsByFromAccountEmail(String userEmail) {
        List<Transaction> transactions = transactionRepository.findByFromAccountEmail(userEmail);
        return transactions.stream().map(TransactionDTO::new).collect(Collectors.toList());
    }
}