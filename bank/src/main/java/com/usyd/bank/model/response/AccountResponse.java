package com.usyd.bank.model.response;

import com.usyd.bank.dto.AccountDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse extends BaseResponse{
    private AccountDTO account;

    public AccountResponse(String message, String responseCode, AccountDTO accountDTO) {
        super(message, responseCode);
        this.account = accountDTO;
    }

    public AccountResponse(String message, String responseCode) {
        super(message, responseCode);
    }


}
