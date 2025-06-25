package com.usyd.deliveryCo.model;

import com.usyd.deliveryCo.model.request.DeliveryRequest;
import com.usyd.deliveryCo.utils.DeliveryStatus;
import com.usyd.deliveryCo.utils.StringListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity object for delivery order database table.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class DeliveryOrder {
    @Id
    @GeneratedValue
    private long id;

    @Version
    private int version;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String toAddress;

    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> fromAddress;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;
    @Column
    private String webhookUrl;

    public DeliveryOrder(DeliveryRequest requestEntity) {
        this.deliveryStatus = DeliveryStatus.CREATED;
        this.userName = requestEntity.getUserName();
        this.quantity = requestEntity.getQuantity();
        this.productName = requestEntity.getProductName();
        this.email = requestEntity.getEmail();
        this.fromAddress = requestEntity.getFromAddress();
        this.toAddress = requestEntity.getToAddress();
        this.creationTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.webhookUrl = requestEntity.getWebhookUrl();
    }
}
