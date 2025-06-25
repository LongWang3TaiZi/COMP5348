package com.usyd.backend.model.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private long userId;
    private long productId;
    private int quantity;
}
