package com.usyd.backend.model.externalResponse;

import com.usyd.backend.dto.WarehouseDTO;
import com.usyd.backend.model.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExternalWarehouseResponse extends BaseResponse {
    private List<WarehouseDTO> warehouses;
    private WarehouseDTO response;

    public ExternalWarehouseResponse() {
        super();
    }

    public ExternalWarehouseResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}
