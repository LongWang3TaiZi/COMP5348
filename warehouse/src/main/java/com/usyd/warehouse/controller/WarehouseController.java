package com.usyd.warehouse.controller;


import com.usyd.warehouse.dto.WarehouseDTO;
import com.usyd.warehouse.dto.WarehouseProductDTO;
import com.usyd.warehouse.model.Warehouse;
import com.usyd.warehouse.model.request.ProductQuantityUpdateRequest;
import com.usyd.warehouse.model.request.UnholdProductRequest;
import com.usyd.warehouse.model.request.WarehouseRequest;
import com.usyd.warehouse.model.response.BaseResponse;
import com.usyd.warehouse.model.response.WarehouseResponse;
import com.usyd.warehouse.service.WarehouseService;
import com.usyd.warehouse.utils.ResponseCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/comp5348/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;
    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    @GetMapping
    public ResponseEntity<WarehouseResponse> getAllWarehouses() {
        logger.info("Received request to get all warehouses");
        List<WarehouseDTO> warehouseDTOS = warehouseService.getAllWarehouses();
        WarehouseResponse response = new WarehouseResponse(warehouseDTOS, ResponseCode.W7.getMessage(),
                ResponseCode.W7.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        logger.info("Received request to get warehouse with id: {}", id);
        WarehouseDTO warehouseDTO = warehouseService.getWarehouseById(id);
        if (warehouseDTO == null) {
            logger.warn("Warehouse not found with id: {}", id);
            WarehouseResponse response = new WarehouseResponse(ResponseCode.W3.getMessage(), ResponseCode.W3.getResponseCode());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        WarehouseResponse response = new WarehouseResponse(warehouseDTO, ResponseCode.W7.getMessage(),
                ResponseCode.W7.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@RequestBody WarehouseRequest warehouse) {
        logger.info("Received request to create warehouse: {}", warehouse.getName());
        WarehouseDTO warehouseDTO = warehouseService.createWarehouse(warehouse);
        if (warehouseDTO == null) {
            logger.warn("Failed to create warehouse: {}", warehouse.getName());
            WarehouseResponse response = new WarehouseResponse(ResponseCode.W4.getMessage(), ResponseCode.W4.getResponseCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        WarehouseResponse response = new WarehouseResponse(warehouseDTO, ResponseCode.W0.getMessage(),
                ResponseCode.W0.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(@PathVariable Long id, @RequestBody Warehouse warehouseDetails) {
        logger.info("Received request to update warehouse with id: {}", id);
        WarehouseDTO warehouseDTO = warehouseService.updateWarehouse(id, warehouseDetails);
        if (warehouseDTO == null) {
            logger.warn("Failed to update warehouse with id: {}", id);
            WarehouseResponse response = new WarehouseResponse(ResponseCode.W5.getMessage(),
                    ResponseCode.W5.getResponseCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        WarehouseResponse response = new WarehouseResponse(warehouseDTO, ResponseCode.W2.getMessage(),
                ResponseCode.W2.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/products/quantity")
    public ResponseEntity<BaseResponse> updateProductQuantities(@RequestBody ProductQuantityUpdateRequest updates) {
        try {
            CompletableFuture<WarehouseProductDTO> warehouseProductDTO = warehouseService.updateProductQuantity(updates);
            if(warehouseProductDTO.get() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(ResponseCode.W5.getMessage(), ResponseCode.W5.getResponseCode()));
            }
            return ResponseEntity.ok().body(new WarehouseResponse(warehouseProductDTO.get().getWarehouse(),
                    ResponseCode.W1.getMessage(), ResponseCode.W1.getResponseCode()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(ResponseCode.W5.getMessage(), ResponseCode.W5.getResponseCode()));
        } catch (OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new BaseResponse(ResponseCode.W5.getMessage(), ResponseCode.W5.getResponseCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(ResponseCode.W5.getMessage(), ResponseCode.W5.getResponseCode()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteWarehouse(@PathVariable Long id) {
        logger.info("Received request to delete warehouse with id: {}", id);
        Boolean response = warehouseService.deleteWarehouse(id);
        if (!response) {
            logger.warn("Failed to delete warehouse with id: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(ResponseCode.W6.getMessage(),
                    ResponseCode.W6.getResponseCode()));
        }
        return ResponseEntity.ok(new BaseResponse(ResponseCode.W2.getMessage(),
                ResponseCode.W2.getResponseCode()));
    }

    @GetMapping("/available/{productId}/{quantity}")
    public ResponseEntity<WarehouseResponse> getAvailableWarehouse(@PathVariable long productId,
                                                                   @PathVariable int quantity) {
        WarehouseDTO warehouseDTOs = warehouseService.getAndUpdateAvailableWarehouse(productId, quantity);
        if (warehouseDTOs == null) return  ResponseEntity.ok(new WarehouseResponse(
                ResponseCode.W8.getMessage(), ResponseCode.W8.getResponseCode()));
        WarehouseResponse response = new WarehouseResponse(warehouseDTOs,
                ResponseCode.W7.getMessage(), ResponseCode.W7.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/unhold")
    public ResponseEntity<WarehouseResponse> unholdProduct(@RequestBody UnholdProductRequest request) {
        boolean result = warehouseService.UnholdProduct(request);
        if (result) return  ResponseEntity.ok(new WarehouseResponse(
                ResponseCode.W9.getMessage(), ResponseCode.W9.getResponseCode()));
        WarehouseResponse response = new WarehouseResponse(
                ResponseCode.W10.getMessage(), ResponseCode.W10.getResponseCode());
        return ResponseEntity.ok(response);
    }
}
