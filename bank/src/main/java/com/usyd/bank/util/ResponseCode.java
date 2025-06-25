package com.usyd.bank.util;

import lombok.Getter;

@Getter
public enum ResponseCode {
    A0("Account create successfully", "A0"),
    A1("Failed to create account", "A1"),
    A3("Account retrieve successfully", "A3"),
    A4("Account not found", "A4"),
    A5("Account top up successfully", "A5"),
    A6("Account top up failed", "A6"),
    T0("Transaction create successfully", "T0"),
    T1("Failed to create transaction", "T1"),
    T2("Insufficient balance", "T2"),
    T3("Refund transaction successfully", "T3"),
    T4("Failed to refund transaction", "T4"),
    T5("Transaction retrieve successfully", "T5"),
    T6("Transaction not found", "T6"),
    C1("Charge back create successfully", "C1"),
    C2("Failed to create charge back" ,"C2");
    private final String message;
    private final String responseCode;

    ResponseCode(String message, String responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }
}
