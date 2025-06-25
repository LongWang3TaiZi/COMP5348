package com.usyd.backend.service.impl;

import com.usyd.backend.dto.ProductDTO;
import com.usyd.backend.model.Product;
import com.usyd.backend.model.externalRequest.ExternalProductRequest;
import com.usyd.backend.model.externalResponse.ExternalProductResponse;
import com.usyd.backend.model.request.AssignProductRequest;
import com.usyd.backend.repository.ProductRepository;
import com.usyd.backend.service.ExternalService;
import com.usyd.backend.service.ProductService;
import com.usyd.backend.utils.FilenameUtils;
import com.usyd.backend.utils.ResponseCode;
import com.usyd.backend.utils.enums.ServiceNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExternalService externalService;


    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        try {
            // get all products from local database
            List<Product> products = productRepository.findAll();
            if (products.size() == 0) return new ArrayList<>();

            // fetch quantities from external warehouse service
            CompletableFuture<ExternalProductResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.PRODUCT,
                    "",
                    HttpMethod.GET,
                    null,
                    ExternalProductResponse.class
            );
            ExternalProductResponse response = futureResponse.get();

            if (response != null) {
                // create map of external product data for efficient lookup
                Map<Long, ProductDTO> externalProductMap = response.getProducts().stream()
                        .collect(Collectors.toMap(ProductDTO::getId, p -> p));

                // combine local product data with external quantities
                return products.stream()
                        .map(localProduct -> {
                            ProductDTO dto = new ProductDTO(localProduct);
                            ProductDTO externalProduct = externalProductMap.get(localProduct.getWarehouseProductId());
                            if (externalProduct != null) {
                                dto.setQuantity(externalProduct.getQuantity());
                            }
                            return dto;
                        })
                        .collect(Collectors.toList());
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        try {
            // find product in local database
            Optional<Product> productOptional = productRepository.findById(id);
            if(!productOptional.isPresent()) return null;

            // fetch quantity from external warehouse service
            CompletableFuture<ExternalProductResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.PRODUCT,
                    "/" + productOptional.get().getWarehouseProductId(),
                    HttpMethod.GET,
                    null,
                    ExternalProductResponse.class
            );
            ExternalProductResponse response = futureResponse.get();
            if (response != null) {
                ProductDTO productDTO = new ProductDTO(productOptional.get());
                productDTO.setQuantity(response.getProduct().getQuantity());
                return productDTO;
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }




    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductDTO createProduct(Product product, MultipartFile file) throws IOException {
        ExternalProductRequest request = new ExternalProductRequest(product.getName());
        try {
            // create product in external warehouse first
            CompletableFuture<ExternalProductResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.PRODUCT,
                    "/create",
                    HttpMethod.POST,
                    request,
                    ExternalProductResponse.class
            );
            ExternalProductResponse response = futureResponse.get();
            if (response != null) {
                try {
                    // handle image upload
                    Resource resource = new ClassPathResource("static/uploads/");
                    Path uploadDir;

                    // determine upload directory path
                    if (resource.exists()) {
                        uploadDir = Paths.get(resource.getFile().getAbsolutePath());
                    } else {
                        uploadDir = Paths.get("src/main/resources/static/uploads/");
                    }

                    // generate unique filename and save file
                    String filename = FilenameUtils.generateUniqueFilename(file.getOriginalFilename());
                    Path filePath = uploadDir.resolve(filename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // save product in local database with image URL and warehouse reference
                    String relativeUrlPath = "/uploads/" + filename;
                    product.setImageUrl(relativeUrlPath);
                    product.setWarehouseProductId(response.getProduct().getId());
                    Product saveedProduct = productRepository.save(product);
                    return new ProductDTO(saveedProduct);
                } catch (IOException e) {
                    logger.warn("Error occoured: {}", e);
                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                } catch (Exception e) {
                    logger.warn("Error occoured: {}", e);
                    throw new RuntimeException("Could not create the product. Error: " + e.getMessage());
                }
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }

    // updates product information both locally and in external warehouse
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProductDTO updateProduct(Long id, Product productDetails) {
        ExternalProductRequest request = new ExternalProductRequest(productDetails.getName());
        Optional<Product> productOptional = productRepository.findById(id);
        if(!productOptional.isPresent()) return null;
        try {
            // update product in external warehouse first
            CompletableFuture<ExternalProductResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.PRODUCT,
                    "/" + productOptional.get().getWarehouseProductId(),
                    HttpMethod.PUT,
                    request,
                    ExternalProductResponse.class
            );
            // if external update successful, update local database
            ExternalProductResponse response = futureResponse.get();
            if (response != null && response.getResult() != null
                    && response.getResult().getResponseCode().equals(ResponseCode.P1.toString())) {
                return productRepository.findById(id)
                        .map(product -> {
                            product.setName(productDetails.getName());
                            product.setDescription(productDetails.getDescription());
                            product.setPrice(productDetails.getPrice());
                            Product updatedProduct = productRepository.save(product);
                            return new ProductDTO(updatedProduct);
                        })
                        .orElse(null);
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }

    // deletes product from both local database and external warehouse
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean deleteProduct(Long id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if(!product.isPresent()) return false;

            // delete from external warehouse first
            CompletableFuture<ExternalProductResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.PRODUCT,
                    "/" + product.get().getWarehouseProductId(),
                    HttpMethod.DELETE,
                    null,
                    ExternalProductResponse.class
            );
            ExternalProductResponse response = futureResponse.get();

            // if external deletion successful, delete from local database
            if (response != null && response.getResult() != null
                    && response.getResult().getResponseCode().equals(ResponseCode.P2.toString())) {

                Optional<Product> productOptional = productRepository.findById(id);
                if (productOptional.isPresent()) {
                    productRepository.deleteById(id);
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return false;
        }
    }

    // assigns product to warehouses in external warehouse system
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean assignProductToWarehouses(Long productId, AssignProductRequest request) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            if(!product.isPresent()) return false;

            // send assignment request to external warehouse service
            CompletableFuture<ExternalProductResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.PRODUCT,
                    "/" + product.get().getWarehouseProductId() + "/assign",
                    HttpMethod.POST,
                    request,
                    ExternalProductResponse.class
            );

            // check if assignment was successful
            ExternalProductResponse response = futureResponse.get();
            if (response != null && response.getResult() != null
                    && response.getResult().getResponseCode().equals(ResponseCode.A2.toString())) {
                        return true;
            }
            return false;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return false;
        }
    }
}