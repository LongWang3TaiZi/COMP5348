package com.usyd.backend.model.externalResponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.backend.model.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCreateAccountResponse extends BaseResponse {
    private AccountResponse account;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class AccountResponse {
        private String accountNumber;
        private String userName;
        private String email;
        private Double balance;
    }
}
