package com.usyd.bank.controller;

import com.usyd.bank.dto.AccountDTO;
import com.usyd.bank.model.request.CreateAccountRequest;
import com.usyd.bank.model.request.TopUpRequest;
import com.usyd.bank.model.response.AccountResponse;
import com.usyd.bank.service.AccountService;
import com.usyd.bank.util.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Interface
 */
@RestController
@RequestMapping("/comp5348/bank/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        AccountDTO account = accountService.createAccount(request);
        if (account != null) {
            return ResponseEntity.ok(new AccountResponse(ResponseCode.A0.getMessage(), ResponseCode.A0.getResponseCode(), account));
        }
        return ResponseEntity.ok(new AccountResponse(ResponseCode.A1.getMessage(), ResponseCode.A1.getResponseCode()));
    }


    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountId) {
        AccountDTO account = accountService.getAccount(accountId);
        if (account == null) ResponseEntity.ok(new AccountResponse(ResponseCode.A4.getMessage(), ResponseCode.A4.getResponseCode()));

        return ResponseEntity.ok(new AccountResponse(ResponseCode.A0.getMessage(), ResponseCode.A0.getResponseCode(), account));
    }

    @PutMapping("/topUp/{accountId}")
    public ResponseEntity<AccountResponse> topUp(
            @PathVariable String accountId, @RequestBody TopUpRequest request) {
        AccountDTO account = accountService.topUp(accountId, request);
        if (account == null) ResponseEntity.ok(new AccountResponse(ResponseCode.A6.getMessage(), ResponseCode.A6.getResponseCode()));

        return ResponseEntity.ok(new AccountResponse(ResponseCode.A5.getMessage(), ResponseCode.A5.getResponseCode(), account));
    }
}
