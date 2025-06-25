package com.usyd.backend.controller;

import com.usyd.backend.dto.WarehouseDTO;
import com.usyd.backend.model.request.WarehouseRequest;
import com.usyd.backend.model.externalRequest.ExternalWarehouseRequest;
import com.usyd.backend.model.response.BaseResponse;
import com.usyd.backend.model.response.WarehouseResponse;
import com.usyd.backend.service.WarehouseService;
import com.usyd.backend.utils.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        WarehouseResponse response = new WarehouseResponse(warehouseDTOS, ResponseCode.W0.getMessage(),
                ResponseCode.W0.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        logger.info("Received request to get warehouse with id: {}", id);
        WarehouseDTO warehouseDTO = warehouseService.getWarehouseById(id);
        if (warehouseDTO == null) {
            logger.warn("WarehouseRequest not found with id: {}", id);
            WarehouseResponse response = new WarehouseResponse(ResponseCode.W3.getMessage(), ResponseCode.W3.getResponseCode());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        WarehouseResponse response = new WarehouseResponse(warehouseDTO, ResponseCode.W0.getMessage(),
                ResponseCode.W0.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@RequestBody ExternalWarehouseRequest request) {
        logger.info("Received request to create warehouse: {}", request.getName());
        WarehouseDTO warehouseDTO = warehouseService.createWarehouse(request);
        if (warehouseDTO == null) {
            logger.warn("Failed to create warehouse: {}", request.getName());
            WarehouseResponse response = new WarehouseResponse(ResponseCode.W4.getMessage(), ResponseCode.W4.getResponseCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        WarehouseResponse response = new WarehouseResponse(warehouseDTO, ResponseCode.W0.getMessage(),
                ResponseCode.W0.getResponseCode());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseRequest warehouseRequestDetails) {
        logger.info("Received request to update warehouse with id: {}", id);
        WarehouseDTO warehouseDTO = warehouseService.updateWarehouse(id, warehouseRequestDetails);
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
}