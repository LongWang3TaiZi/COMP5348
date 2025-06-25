package com.usyd.warehouse.model.response;

import com.usyd.warehouse.dto.WarehouseDTO;
import com.usyd.warehouse.model.Warehouse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WarehouseResponse extends BaseResponse {
    private List<WarehouseDTO> warehouses;
    private WarehouseDTO response;

    public WarehouseResponse(Warehouse warehouse, String message, String responseCode) {
        super(message, responseCode);
        if (warehouse != null) this.response = new WarehouseDTO(warehouse);
    }

    public WarehouseResponse(List<WarehouseDTO> warehouse, String message, String responseCode) {
        super(message, responseCode);
        if (warehouse != null) this.warehouses = warehouse;
    }

    public WarehouseResponse(WarehouseDTO warehouse, String message, String responseCode) {
        super(message, responseCode);
        if (warehouse != null) this.response = warehouse;
    }

    public WarehouseResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}

