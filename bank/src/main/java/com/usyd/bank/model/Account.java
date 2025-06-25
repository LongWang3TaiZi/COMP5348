package com.usyd.bank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

/**
 * Entity object for account database table.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {
    @Id
    private String accountNumber;

    @Version
    private int version;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Double balance = 0.0;

    private LocalDateTime modifyTime;

    public Account(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void updateBalance(Double amount){
        this.balance += amount;
    }
}
