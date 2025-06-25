package com.usyd.backend.service;

import com.usyd.backend.dto.WarehouseDTO;
import com.usyd.backend.model.request.WarehouseRequest;
import com.usyd.backend.model.externalRequest.ExternalWarehouseRequest;
import jakarta.persistence.OptimisticLockException;

import java.util.List;

public interface WarehouseService {
    List<WarehouseDTO> getAllWarehouses();
    WarehouseDTO getWarehouseById(Long id);
    WarehouseDTO createWarehouse(ExternalWarehouseRequest request);
    WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequest) throws OptimisticLockException;
    Boolean deleteWarehouse(Long id) throws OptimisticLockException;
}