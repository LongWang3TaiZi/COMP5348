package com.usyd.bank.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.bank.dto.TransactionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse extends BaseResponse{
    private TransactionDTO transaction;
    private List<TransactionDTO> transactions;
    public TransactionResponse(String message, String responseCode) {
        super(message, responseCode);
    }

    public TransactionResponse(String message, String responseCode, TransactionDTO transactionDTO) {
        super(message, responseCode);
        this.transaction = transactionDTO;
    }

    public TransactionResponse(String message, String responseCode, List<TransactionDTO> transactionDTOs) {
        super(message, responseCode);
        this.transactions = transactionDTOs;
    }
}
