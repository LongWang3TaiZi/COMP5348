package com.usyd.backend.model.externalResponse;

import com.usyd.backend.model.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCreateTransactionResponse extends BaseResponse {
    private TransactionResponse transaction;
}
