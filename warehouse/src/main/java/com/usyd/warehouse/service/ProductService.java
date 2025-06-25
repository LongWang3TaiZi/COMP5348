package com.usyd.warehouse.service;

import com.usyd.warehouse.controller.ProductController;
import com.usyd.warehouse.dto.ProductDTO;
import com.usyd.warehouse.model.Product;
import com.usyd.warehouse.model.Warehouse;
import com.usyd.warehouse.model.WarehouseProduct;
import com.usyd.warehouse.model.request.AssignProductRequest;
import com.usyd.warehouse.model.request.ProductRequest;
import com.usyd.warehouse.repository.ProductRepository;
import com.usyd.warehouse.repository.WarehouseProductRepository;
import com.usyd.warehouse.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseProductRepository warehouseProductRepository;

    @Autowired
    private WarehouseService warehouseService;

    // retrieves all products with their total quantities
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::createProductDTOWithQuantity)
                .collect(Collectors.toList());

        return productDTOs;
    }

    // retrieves a specific product by ID with its total quantity
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            int quantity = warehouseService.getProductQuantity(productOptional.get().getId());
            ProductDTO productDTO = new ProductDTO(productOptional.get());
            productDTO.setQuantity(quantity);
            return productDTO;
        }
        logger.warn("product not found with id: {}", id);
        return null;
    }


    // async creates a new product
    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CompletableFuture<ProductDTO> createProduct(ProductRequest request) throws IOException {
        Product product = new Product();
        product.setName(request.getName());
        product.setModifyTime(LocalDateTime.now());
        return CompletableFuture.supplyAsync(() -> {
            try {
                Product savedProduct = productRepository.save(product);
                return new ProductDTO(savedProduct);
            }  catch (Exception e) {
                logger.error("failed to create product with name {}: {}", request.getName(), e.getMessage());
                throw new RuntimeException("could not create the product. Error: " + e.getMessage());
            }
        });
    }

    // updates an existing product
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .map(product -> {
                    try {
                        product.setName(request.getName());
                        product.setModifyTime(LocalDateTime.now());
                        Product updatedProduct = productRepository.save(product);
                        return new ProductDTO(updatedProduct);
                    } catch (Exception e) {
                        logger.error("failed to update product {}: {}", id, e.getMessage());
                        return null;
                    }
                })
                .orElseGet(() -> {
                    logger.warn("product not found for update with id: {}", id);
                    return null;
                });
    }

    // deletes a product
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    try {
                        productRepository.delete(product);
                        return true;
                    } catch (Exception e) {
                        logger.error("failed to delete product {}: {}", id, e.getMessage());
                        return false;
                    }
                })
                .orElseGet(() -> {
                    logger.warn("product not found for deletion with id: {}", id);
                    return false;
                });
    }

    // assigns products to warehouses with quantity
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean assignProductToWarehouses(Long productId, AssignProductRequest request) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        logger.error("product not found with id: {}", productId);
                        return new RuntimeException("Product not found with id: " + productId);
                    });

            List<WarehouseProduct> warehouseProducts = new ArrayList<>();

            for (AssignProductRequest.WarehouseAssignment assignment : request.getAssignments()) {
                Warehouse warehouse = warehouseRepository.findById(assignment.getWarehouseId())
                        .orElseThrow(() -> {
                            logger.error("warehouse not found with id: {}", assignment.getWarehouseId());
                            return new RuntimeException("Warehouse not found with id: " + assignment.getWarehouseId());
                        });

                // check if warehouse have the product
                Optional<WarehouseProduct> existingWarehouseProduct = warehouseProductRepository
                        .findByWarehouseAndProduct(warehouse, product);

                WarehouseProduct warehouseProduct;

                if (existingWarehouseProduct.isPresent()) {
                    // if exists update quantity
                    warehouseProduct = existingWarehouseProduct.get();
                    int newQuantity = warehouseProduct.getQuantity() + assignment.getQuantity();


                    warehouseProduct.setQuantity(newQuantity);
                } else {
                    // not exists create new
                    warehouseProduct = new WarehouseProduct();
                    warehouseProduct.setProduct(product);
                    warehouseProduct.setWarehouse(warehouse);
                    warehouseProduct.setQuantity(assignment.getQuantity());
                }
                warehouseProduct.setModifyTime(LocalDateTime.now());
                warehouseProducts.add(warehouseProductRepository.save(warehouseProduct));
            }

            return true;
        } catch (Exception e) {
            logger.error("error assigning product {} to warehouses: {}", productId, e.getMessage());
            throw new RuntimeException("Error assigning product to warehouses", e);
        }
    }

    // helper method to create product DTO with total quantity
    private ProductDTO createProductDTOWithQuantity(Product product) {
        try {
            int quantity = warehouseService.getProductQuantity(product.getId());
            ProductDTO dto = new ProductDTO(product);
            dto.setQuantity(quantity);
            return dto;
        } catch (Exception e) {
            logger.error("failed to create product DTO with quantity for product {}: {}",
                    product.getId(), e.getMessage());
            throw e;
        }
    }
}
