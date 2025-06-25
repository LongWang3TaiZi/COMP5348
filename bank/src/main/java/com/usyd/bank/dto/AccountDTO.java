package com.usyd.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.usyd.bank.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Account.
 */
@Getter
@Setter
@JsonInclude(Include.NON_EMPTY)
public class AccountDTO {
    private String accountNumber;
    private String userName;
    private String email;
    private Double balance;


    /**
     * Constructs an AccountDTO from an Account entity.
     *
     * @param accountEntity the account entity
     */
    public AccountDTO(Account accountEntity) {
        this.accountNumber = accountEntity.getAccountNumber();
        this.userName = accountEntity.getUsername();
        this.email = accountEntity.getEmail();
        this.balance = accountEntity.getBalance();
    }
}