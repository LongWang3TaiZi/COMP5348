package com.usyd.warehouse.service;

import com.usyd.warehouse.dto.ProductDTO;
import com.usyd.warehouse.dto.WarehouseDTO;
import com.usyd.warehouse.dto.WarehouseProductDTO;
import com.usyd.warehouse.model.InventoryTransaction;
import com.usyd.warehouse.model.Warehouse;
import com.usyd.warehouse.model.WarehouseProduct;
import com.usyd.warehouse.model.request.ProductQuantityUpdateRequest;
import com.usyd.warehouse.model.request.UnholdProductRequest;
import com.usyd.warehouse.model.request.WarehouseRequest;
import com.usyd.warehouse.repository.InventoryTransactionRepository;
import com.usyd.warehouse.repository.WarehouseProductRepository;
import com.usyd.warehouse.repository.WarehouseRepository;
import com.usyd.warehouse.utils.InventoryTransactionType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseProductRepository warehouseProductRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    // retrieves all warehouses and converts them to DTOs
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<WarehouseDTO> warehouseDTOs = warehouses.stream()
                .map(WarehouseDTO::new)
                .collect(Collectors.toList());
        return warehouseDTOs;
    }

    // retrieves a specific warehouse by ID along with its products
    @Transactional()
    public WarehouseDTO getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .map(warehouse -> {
                    WarehouseDTO warehouseDTO = new WarehouseDTO(warehouse);
                    List<WarehouseProduct> warehouseProducts = warehouseProductRepository.findByWarehouseId(warehouse.getId());
                    List<ProductDTO> productDTOs = warehouseProducts.stream()
                            .map(wp -> {
                                ProductDTO productDTO = new ProductDTO(wp.getProduct());
                                productDTO.setQuantity(wp.getQuantity());
                                return productDTO;
                            })
                            .collect(Collectors.toList());
                    warehouseDTO.setProducts(productDTOs);
                    return warehouseDTO;
                })
                .orElse(null);
    }

     // gets total quantity of a product across all warehouses
    @Transactional(readOnly = true)
    public int getProductQuantity(Long productId) {
        return warehouseProductRepository.findTotalQuantityByProductId(productId).orElse(0);
    }

    // creates a new warehouse
    public WarehouseDTO createWarehouse(WarehouseRequest request) {
        try {
            Warehouse warehouse = new Warehouse(request.getName(), request.getLocation());
            warehouse.setModifyTime(LocalDateTime.now());
            Warehouse savedWarehouse = warehouseRepository.save(warehouse);
            return new WarehouseDTO(savedWarehouse);
        } catch (Exception e) {
            logger.error("An error occoured: {}", e.toString());
            return null;
        }
    }

    // async updates product quantities with optimistic locking
    @Async
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CompletableFuture<WarehouseProductDTO> updateProductQuantity(ProductQuantityUpdateRequest updates) {
        return CompletableFuture.supplyAsync(() -> {
            for (ProductQuantityUpdateRequest.ProductQuantityUpdate update : updates.getUpdates()) {
                try {
                    WarehouseProduct warehouseProduct = warehouseProductRepository
                            .findByWarehouseIdAndProductId(update.getWarehouseId(), update.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("WarehouseProduct not found"));

                    warehouseProduct.setQuantity(update.getNewQuantity());

                    warehouseProduct.setModifyTime(LocalDateTime.now());
                    WarehouseProduct savedWarehouseProduct = warehouseProductRepository.save(warehouseProduct);

                    InventoryTransaction transaction = new InventoryTransaction();
                    transaction.setProduct(savedWarehouseProduct.getProduct());
                    transaction.setWarehouse(savedWarehouseProduct.getWarehouse());
                    transaction.setQuantity(update.getNewQuantity());
                    transaction.setTransactionTime(LocalDateTime.now());
                    transaction.setType(InventoryTransactionType.IN);

                    return new WarehouseProductDTO(warehouseProduct);
                } catch (OptimisticLockException e) {
                    logger.error("optimistic lock exception during quantity update: {}", e.getMessage());
                    throw e;
                }

            }
            return null;
        });
    }

    // updates warehouse information with optimistic locking
    public WarehouseDTO updateWarehouse(Long id, Warehouse warehouseDetails) throws OptimisticLockException {
        return warehouseRepository.findByIdWithLock(id)
                .map(warehouse -> {
                    warehouse.setName(warehouseDetails.getName());
                    warehouse.setLocation(warehouseDetails.getLocation());
                    try {
                        warehouse.setModifyTime(LocalDateTime.now());
                        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
                        return new WarehouseDTO(updatedWarehouse);
                    } catch (OptimisticLockException e) {
                        logger.error("optimistic lock exception during warehouse update: {}", e.getMessage());
                        throw e;
                    } catch (Exception e) {
                        logger.error("failed to update warehouse: {}", e.getMessage());
                        return null;
                    }
                })
                .orElse(null);
    }

    // deletes a warehouse with optimistic locking
    public Boolean deleteWarehouse(Long id) throws OptimisticLockException {
        return warehouseRepository.findByIdWithLock(id)
                .map(warehouse -> {
                    try {
                        warehouseRepository.delete(warehouse);
                        return true;
                    } catch (OptimisticLockException e) {
                        logger.error("optimistic lock exception during warehouse deletion: {}", e.getMessage());
                        return false;
                    } catch (Exception e) {
                        logger.error("failed to delete warehouse: {}", e.getMessage());
                        return false;
                    }
                })
                .orElse(false);
    }

    // finds available warehouses for a product and updates inventory accordingly
    public WarehouseDTO getAndUpdateAvailableWarehouse(long productId, int quantity){
        List<WarehouseProduct> availableProducts = warehouseProductRepository.findByProductIdAndQuantity(productId);
        List<WarehouseDTO> warehouseDTOs = new ArrayList<>();
        List<Long> inventoryTransactionIds = new ArrayList<>();
        int remainingQuantity = quantity;
        List<WarehouseProduct> updatedProducts = new ArrayList<>();

        for (WarehouseProduct wp : availableProducts) {
            if (remainingQuantity <= 0) {
                break;
            }

            int availableQuantity = wp.getQuantity();
            int quantityToTake = Math.min(availableQuantity, remainingQuantity);

            warehouseDTOs.add(new WarehouseDTO(wp.getWarehouse()));
            remainingQuantity -= quantityToTake;

            WarehouseProduct updatedWp = new WarehouseProduct(wp);
            updatedWp.setQuantity(availableQuantity - quantityToTake);
            updatedProducts.add(updatedWp);

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setProduct(wp.getProduct());
            transaction.setWarehouse(wp.getWarehouse());
            transaction.setQuantity(quantityToTake);
            transaction.setTransactionTime(LocalDateTime.now());
            transaction.setType(InventoryTransactionType.HOLD);
            InventoryTransaction savedTransaction = inventoryTransactionRepository.save(transaction);
            inventoryTransactionIds.add(savedTransaction.getId());
        }

        if (remainingQuantity > 0) {
            logger.warn("insufficient stock for product: {}, remaining quantity needed: {}", productId, remainingQuantity);
            return null; // not enough stock return null
        }

        // only order can be fulfilled, update the stock level
        for (WarehouseProduct wp : updatedProducts) {
            warehouseProductRepository.save(wp);
        }
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setWarehouses(warehouseDTOs);
        warehouseDTO.setInventoryTransactionIds(inventoryTransactionIds);
        return warehouseDTO;
    }

    // releases held products back to inventory
    public boolean UnholdProduct(UnholdProductRequest request){
        List<Long> transactionIds = request.getInventoryTransactionIds();

        if (transactionIds == null || transactionIds.isEmpty()) {
            return false;
        }

        try {
            List<InventoryTransaction> transactions = inventoryTransactionRepository.findAllById(transactionIds);

            for (InventoryTransaction transaction : transactions) {


                // update WarehouseProduct
                Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepository.findByWarehouseAndProduct(
                        transaction.getWarehouse(), transaction.getProduct());

                if (!warehouseProductOptional.isPresent()) {
                    return false;
                }

                WarehouseProduct warehouseProduct = warehouseProductOptional.get();


                warehouseProduct.setQuantity(warehouseProduct.getQuantity() + transaction.getQuantity());
                warehouseProduct.setModifyTime(LocalDateTime.now());
                warehouseProductRepository.save(warehouseProduct);

                // update InventoryTransaction
                transaction.setType(InventoryTransactionType.UNHOLD);
                transaction.setTransactionTime(LocalDateTime.now());
                transaction.setTransactionTime(LocalDateTime.now());
                inventoryTransactionRepository.save(transaction);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
