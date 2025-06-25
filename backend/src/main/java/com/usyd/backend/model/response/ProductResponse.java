package com.usyd.backend.model.response;

import com.usyd.backend.dto.ProductDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductResponse extends BaseResponse {
    private List<ProductDTO> products;
    private ProductDTO product;

    public ProductResponse(List<ProductDTO> products, String message, String responseCode) {
        super(message, responseCode);
        this.products = products;
    }
    public ProductResponse(ProductDTO products, String message, String responseCode) {
        super(message, responseCode);
        this.product = products;
    }
    public ProductResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}