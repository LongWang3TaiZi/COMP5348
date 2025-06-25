package com.usyd.warehouse.controller;

import com.usyd.warehouse.dto.ProductDTO;
import com.usyd.warehouse.model.request.AssignProductRequest;
import com.usyd.warehouse.model.request.ProductRequest;
import com.usyd.warehouse.model.response.BaseResponse;
import com.usyd.warehouse.model.response.ProductResponse;
import com.usyd.warehouse.service.ProductService;
import com.usyd.warehouse.utils.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/comp5348/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    public ResponseEntity<ProductResponse> getAllProducts() {
        logger.info("Received request to get all products");
        List<ProductDTO> productDTOs = productService.getAllProducts();
        ProductResponse response = new ProductResponse(productDTOs, ResponseCode.P7.getMessage(),
                ResponseCode.P7.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        logger.info("Received request to get product with id: {}", id);
        ProductDTO productDTO = productService.getProductById(id);
        if (productDTO == null) {
            logger.warn("Product not found with id: {}", id);
            ProductResponse response = new ProductResponse(ResponseCode.P3.getMessage(), ResponseCode.P3.getResponseCode());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ProductResponse response = new ProductResponse(productDTO, ResponseCode.P7.getMessage(),
                ResponseCode.P7.getResponseCode());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ProductResponse>> createProduct(
            @RequestBody ProductRequest request) throws IOException {
        logger.info("Received request to create product with name：{}", request.getName());
        return productService.createProduct(request)
                .thenApply(productDTO -> {
                    if (productDTO == null) {
                        logger.warn("Failed to create product with name：{}", request.getName());
                        ProductResponse response = new ProductResponse(ResponseCode.P4.getMessage(),
                                ResponseCode.P4.getResponseCode());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                    ProductResponse response = new ProductResponse(productDTO,
                            ResponseCode.P0.getMessage(), ResponseCode.P0.getResponseCode());
                    return ResponseEntity.ok(response);
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        logger.info("Received request to update product with id: {}", id);
        ProductDTO productDTO = productService.updateProduct(id, request);
        if (productDTO == null) {
            logger.warn("Failed to update product with id: {}", id);
            ProductResponse response = new ProductResponse(ResponseCode.P3.getMessage(), ResponseCode.P3.getResponseCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        ProductResponse response = new ProductResponse(productDTO, ResponseCode.P1.getMessage(), ResponseCode.P1.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteProduct(@PathVariable Long id) {
        logger.info("Received request to delete product with id: {}", id);
        Boolean response = productService.deleteProduct(id);
        if (!response) {
            logger.warn("Failed to delete product with id: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse(ResponseCode.P3.getMessage(), ResponseCode.P3.getResponseCode()));
        }
        return ResponseEntity.ok(new BaseResponse(ResponseCode.P2.getMessage(), ResponseCode.P2.getResponseCode()));
    }

    @PostMapping("/{productId}/assign")
    public ResponseEntity<BaseResponse> assignProductToWarehouses(@PathVariable Long productId,
                                                                     @RequestBody AssignProductRequest request) {
        logger.info("Received request to assign product {} to multiple warehouses", productId);
        Boolean assignResponse = productService.assignProductToWarehouses(productId, request);
        if (!assignResponse) {
            logger.warn("Failed to assign product {} to warehouses", productId);
            ProductResponse response = new ProductResponse(ResponseCode.A3.getMessage(), ResponseCode.A3.getResponseCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        BaseResponse response = new BaseResponse(ResponseCode.A2.getMessage(),
                ResponseCode.A2.getResponseCode());
        return ResponseEntity.ok(response);
    }
}
