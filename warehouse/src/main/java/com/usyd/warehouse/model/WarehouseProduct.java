package com.usyd.warehouse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonBackReference
    private Warehouse warehouse;
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    private Integer quantity;

    @Version
    private Long version;

    private LocalDateTime modifyTime;

    public WarehouseProduct(WarehouseProduct warehouseProductEntity) {
        this.id = warehouseProductEntity.id;
        this.warehouse = warehouseProductEntity.warehouse;
        this.product = warehouseProductEntity.product;
        this.quantity = warehouseProductEntity.quantity;
        this.version = warehouseProductEntity.version;
        this.modifyTime = warehouseProductEntity.getModifyTime();
    }
}
