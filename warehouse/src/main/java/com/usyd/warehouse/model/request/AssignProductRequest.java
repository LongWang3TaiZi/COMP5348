package com.usyd.warehouse.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignProductRequest {
    private List<WarehouseAssignment> assignments;

    @Getter
    @Setter
    public static class WarehouseAssignment {
        private Long warehouseId;
        private Integer quantity;
    }
}
