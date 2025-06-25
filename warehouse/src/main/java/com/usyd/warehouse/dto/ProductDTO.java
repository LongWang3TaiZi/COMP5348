package com.usyd.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.warehouse.model.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private Long id;
    private String name;
    private int quantity;

    public ProductDTO(Product productEntity) {
        this.id = productEntity.getId();
        this.name = productEntity.getName();
    }
}
