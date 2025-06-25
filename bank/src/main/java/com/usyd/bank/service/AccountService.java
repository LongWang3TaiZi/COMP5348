package com.usyd.bank.service;

import com.usyd.bank.dto.AccountDTO;
import com.usyd.bank.model.Account;
import com.usyd.bank.model.request.CreateAccountRequest;
import com.usyd.bank.model.request.TopUpRequest;
import com.usyd.bank.repository.AccountRepository;
import jakarta.transaction.Transactional;
import com.usyd.bank.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Business logic for account management.
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    @Transactional
    public AccountDTO createAccount(CreateAccountRequest request) {
        Account account = new Account(request.getUsername(), request.getEmail());
        account.setAccountNumber(Util.generateAccountNumber());
        account.setModifyTime(LocalDateTime.now());
        Account savedAccount = accountRepository.save(account);
        if (savedAccount != null){
            return new AccountDTO(savedAccount);
        }
        return null;
    }

    @Transactional
    public AccountDTO getAccount(String accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) return null;

        AccountDTO accountDTO = new AccountDTO(account.get());
        return accountDTO;
    }

    @Transactional
    public AccountDTO topUp(String accountNumber, TopUpRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        account.setModifyTime(LocalDateTime.now());
        if (account == null) return null;
        account.updateBalance(request.getAmount());
        account.setModifyTime(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(account);
        if (updatedAccount != null){
            return new AccountDTO(updatedAccount);
        }
        return null;
    }
}