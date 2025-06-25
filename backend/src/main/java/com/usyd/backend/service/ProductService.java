package com.usyd.backend.service;

import com.usyd.backend.dto.ProductDTO;
import com.usyd.backend.model.Product;
import com.usyd.backend.model.request.AssignProductRequest;
import com.usyd.backend.model.response.BaseResponse;
import com.usyd.backend.model.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(Product product, MultipartFile file) throws IOException;
    ProductDTO updateProduct(Long id, Product productDetails);
    Boolean deleteProduct(Long id);
    Boolean assignProductToWarehouses(Long productId, AssignProductRequest request);
}