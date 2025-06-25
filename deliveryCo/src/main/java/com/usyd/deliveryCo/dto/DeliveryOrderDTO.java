package com.usyd.deliveryCo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.deliveryCo.model.DeliveryOrder;
import com.usyd.deliveryCo.utils.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Data Transfer Object for DeliveryOrder.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryOrderDTO {
    private long id;
    private String productName;
    private int quantity;
    private String email;
    private DeliveryStatus deliveryStatus;
    private LocalDateTime creationTime;
    private LocalDateTime updateTime;
    private String toAddress;
    private List<String> fromAddress;

    /**
     * Constructs an DeliveryOrderDTO from an DeliveryOrder entity.
     *
     * @param entity the delivery order entity
     */
    public DeliveryOrderDTO(DeliveryOrder entity) {
        this.id = entity.getId();
        this.productName = entity.getProductName();
        this.quantity = entity.getQuantity();
        this.email = entity.getEmail();
        this.deliveryStatus = entity.getDeliveryStatus();
        this.creationTime = entity.getCreationTime();
        this.updateTime = entity.getUpdateTime();
        this.toAddress = entity.getToAddress();
        this.fromAddress = entity.getFromAddress();
    }
}
