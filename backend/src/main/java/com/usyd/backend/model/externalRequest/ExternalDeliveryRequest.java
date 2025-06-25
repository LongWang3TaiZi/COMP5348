package com.usyd.backend.model.externalRequest;

import com.usyd.backend.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExternalDeliveryRequest {
    private String email;
    private String userName;
    private String toAddress;
    private List<String> fromAddress;
    private String productName;
    private int quantity;
    private final String webhookUrl = "http://localhost:8080/comp5348/webhook";

    public ExternalDeliveryRequest(Order orderEntity) {
        this.email = orderEntity.getUser().getEmail();
        this.userName = orderEntity.getUser().getUsername();
        this.toAddress = orderEntity.getUser().getAddress().toString();
        this.fromAddress = orderEntity.getAvailableWarehouse();
        this.productName = orderEntity.getProduct().getName();
        this.quantity = orderEntity.getQuantity();
    }
}
