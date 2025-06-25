package com.usyd.warehouse.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantityUpdateRequest {
    private List<ProductQuantityUpdate> updates;
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductQuantityUpdate {
        private Long warehouseId;
        private Long productId;
        private int newQuantity;
    }
}
