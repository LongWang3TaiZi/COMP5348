package com.usyd.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.backend.model.request.WarehouseRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private List<ProductDTO> products;
    private List<WarehouseDTO> warehouses;
    List<Long> inventoryTransactionIds;
}
