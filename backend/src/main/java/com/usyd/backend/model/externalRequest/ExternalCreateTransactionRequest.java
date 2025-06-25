package com.usyd.backend.model.externalRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCreateTransactionRequest {
    private double amount;
    private String fromAccount;
    private String toAccount;
}
