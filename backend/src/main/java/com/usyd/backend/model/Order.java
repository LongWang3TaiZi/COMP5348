package com.usyd.backend.model;

import com.usyd.backend.model.request.OrderRequest;
import com.usyd.backend.utils.StringListConverter;
import com.usyd.backend.utils.enums.DeliveryStatus;
import com.usyd.backend.utils.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue
    private long id;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliverStatus;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> availableWarehouse;

    @Column
    private long transactionId;

    @ElementCollection
    @CollectionTable(name = "order_inventory_transaction_ids", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "inventory_transaction_id")
    private List<Long> inventoryTransactionIds;

    @Column
    private String trackingNumber;

    @Column
    private String paymentStatus;

    public Order(OrderRequest orderEntity) {
        this.quantity = orderEntity.getQuantity();
        this.orderStatus = OrderStatus.AWAITING_PAYMENT;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
