package com.usyd.warehouse.model.response;

import com.usyd.warehouse.dto.ProductDTO;
import com.usyd.warehouse.dto.WarehouseProductDTO;
import com.usyd.warehouse.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductResponse extends BaseResponse {
    private List<ProductDTO> products;
    private List<WarehouseProductDTO> warehouseProducts;
    private ProductDTO product;

    public ProductResponse(List<ProductDTO> products, String message, String responseCode) {
        super(message, responseCode);
        this.products = products;
    }

    public ProductResponse(List<WarehouseProductDTO> warehouseProductDTOs, String message, String responseCode, boolean isWarehouseProduct) {
        super(message, responseCode);
        this.warehouseProducts = warehouseProductDTOs;
    }


    public ProductResponse(ProductDTO products, String message, String responseCode) {
        super(message, responseCode);
        this.product = products;
    }

    public ProductResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}
