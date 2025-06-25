package com.usyd.bank.controller;

import com.usyd.bank.dto.TransactionDTO;
import com.usyd.bank.model.request.CreateTransactionRequest;
import com.usyd.bank.model.response.TransactionResponse;
import com.usyd.bank.service.TransactionService;
import com.usyd.bank.util.ResponseCode;
import com.usyd.bank.util.TransactionStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comp5348/bank/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request) {
        TransactionDTO transactionDTO = transactionService.createPayment(request);
        if (transactionDTO == null) {
            return ResponseEntity.ok(new TransactionResponse(ResponseCode.T1.getMessage(), ResponseCode.T1.getResponseCode()));
        }
        if (transactionDTO.getTransactionStatus() == TransactionStatus.InsufficientBalance) {
            return ResponseEntity.ok(new TransactionResponse(ResponseCode.T2.getMessage(), ResponseCode.T2.getResponseCode(), transactionDTO));
        }
        return ResponseEntity.ok(new TransactionResponse(ResponseCode.T0.getMessage(), ResponseCode.T0.getResponseCode(), transactionDTO));

    }

    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<TransactionResponse> refundTransaction(@PathVariable String transactionId) {
        TransactionDTO transactionDTO = transactionService.refundTransaction(transactionId);
        if (transactionDTO == null) {
            return ResponseEntity.ok(new TransactionResponse(ResponseCode.T4.getMessage(), ResponseCode.T4.getResponseCode()));
        }
        return ResponseEntity.ok(new TransactionResponse(ResponseCode.T3.getMessage(), ResponseCode.T3.getResponseCode(), transactionDTO));
    }

    @PostMapping("/charge-back/{refundId}")
    public ResponseEntity<TransactionResponse> chargeBackTransaction(@PathVariable String refundId) {
        boolean result = transactionService.chargeBackTransaction(refundId);
        if (!result) {
            return ResponseEntity.ok(new TransactionResponse(ResponseCode.C2.getMessage(), ResponseCode.C2.getResponseCode()));
        }
        return ResponseEntity.ok(new TransactionResponse(ResponseCode.C1.getMessage(), ResponseCode.C1.getResponseCode()));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId) {
        TransactionDTO transactionDTO = transactionService.getTransaction(transactionId);
        if (transactionDTO == null) {
            return ResponseEntity.ok(new TransactionResponse(ResponseCode.T6.getMessage(), ResponseCode.T6.getResponseCode()));
        }
        return ResponseEntity.ok(new TransactionResponse(ResponseCode.T5.getMessage(), ResponseCode.T5.getResponseCode(), transactionDTO));
    }

    @GetMapping("/all/{fromAccountEmail}")
    public ResponseEntity<TransactionResponse> getTransactionsByFromAccountEmail(@PathVariable String fromAccountEmail) {
        List<TransactionDTO> transactionDTOs = transactionService.getTransactionsByFromAccountEmail(fromAccountEmail);
        if (transactionDTOs == null) {
            return ResponseEntity.ok(new TransactionResponse(ResponseCode.T6.getMessage(), ResponseCode.T6.getResponseCode()));
        }
        return ResponseEntity.ok(new TransactionResponse(ResponseCode.T5.getMessage(), ResponseCode.T5.getResponseCode(), transactionDTOs));
    }
}
