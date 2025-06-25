package com.usyd.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.backend.model.Product;
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
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private int quantity;

    public ProductDTO(Product productEntity) {
        this.id = productEntity.getId();
        this.name = productEntity.getName();
        this.description = productEntity.getDescription();
        this.price = productEntity.getPrice();
        this.imageUrl = productEntity.getImageUrl();
    }
}
