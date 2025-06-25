package com.usyd.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.warehouse.model.Product;
import com.usyd.warehouse.model.Warehouse;
import com.usyd.warehouse.model.WarehouseProduct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseProductDTO {
    private Long id;
    private Warehouse warehouse;
    private Product product;
    private Integer quantity;

    public WarehouseProductDTO(WarehouseProduct warehouseProductEntity) {
        this.id = warehouseProductEntity.getId();
        this.warehouse = warehouseProductEntity.getWarehouse();
        this.product = warehouseProductEntity.getProduct();
        this.quantity = warehouseProductEntity.getQuantity();
    }
}
