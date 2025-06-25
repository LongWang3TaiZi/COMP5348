package com.usyd.backend.model.response;

import com.usyd.backend.dto.WarehouseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WarehouseResponse extends BaseResponse {
    private List<WarehouseDTO> warehouses;
    private WarehouseDTO warehouse;

    public WarehouseResponse(List<WarehouseDTO> warehouse, String message, String responseCode) {
        super(message, responseCode);
        if (warehouse != null) this.warehouses = warehouse;
    }

    public WarehouseResponse(WarehouseDTO warehouse, String message, String responseCode) {
        super(message, responseCode);
        if (warehouse != null) this.warehouse = warehouse;
    }

    public WarehouseResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}
