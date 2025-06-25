package com.usyd.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.backend.model.Order;
import com.usyd.backend.utils.enums.DeliveryStatus;
import com.usyd.backend.utils.enums.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrderDTO {
    private long id;
    private int quantity;
    private String productName;
    private String productImgURL;
    private long productId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String paymentStatus;
    private DeliveryStatus deliverStatus;
    private OrderStatus orderStatus;
    private String trackingNumber;

    public OrderDTO(Order orderEntity) {
        this.id = orderEntity.getId();
        this.quantity = orderEntity.getQuantity();
        this.productName = orderEntity.getProduct().getName();
        this.productImgURL = orderEntity.getProduct().getImageUrl();
        this.productId = orderEntity.getProduct().getId();
        this.createTime = orderEntity.getCreateTime();
        this.updateTime = orderEntity.getUpdateTime();
        this.paymentStatus = orderEntity.getPaymentStatus();
        this.deliverStatus = orderEntity.getDeliverStatus();
        this.orderStatus = orderEntity.getOrderStatus();
        this.trackingNumber = orderEntity.getTrackingNumber();
    }
}
