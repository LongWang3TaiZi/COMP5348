package com.usyd.backend.model.externalRequest;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExternalOrderRequest {
    private String email;
    private String userName;
    private String toAddress;
    private List<String> fromAddress;
    private String productName;
    private int quantity;
    private String webhookUrl;
}
