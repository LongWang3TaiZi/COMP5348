package com.usyd.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.warehouse.model.Warehouse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private List<ProductDTO> products;
    List<WarehouseDTO> warehouses;
    List<Long> inventoryTransactionIds;


    public WarehouseDTO(Warehouse warehouseEntity) {
        this.id = warehouseEntity.getId();
        this.name = warehouseEntity.getName();
        this.location = warehouseEntity.getLocation();
    }
}
