package com.usyd.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.backend.model.externalResponse.ExternalDeliveryResponse;
import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryInformationDTO {
    private long id;
    private String productName;
    private int quantity;
    private String email;
    private String deliveryStatus;
    private String creationTime;
    private String updateTime;
    private String toAddress;
    private List<String> fromAddress;

    public DeliveryInformationDTO(ExternalDeliveryResponse response) {
        this.id = response.getId();
        this.productName = response.getProductName();
        this.quantity = response.getQuantity();
        this.email = response.getEmail();
        this.deliveryStatus = response.getDeliveryStatus();
        this.creationTime = response.getCreationTime();
        this.updateTime = response.getUpdateTime();
        this.fromAddress = response.getFromAddress();
        this.toAddress = response.getToAddress();
    }
}
