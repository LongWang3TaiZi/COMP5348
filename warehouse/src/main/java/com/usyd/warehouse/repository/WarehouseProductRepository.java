package com.usyd.warehouse.repository;

import com.usyd.warehouse.model.Product;
import com.usyd.warehouse.model.Warehouse;
import com.usyd.warehouse.model.WarehouseProduct;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT wp FROM WarehouseProduct wp WHERE wp.warehouse.id = :warehouseId AND wp.product.id = :productId")
    Optional<WarehouseProduct> findByWarehouseIdAndProductId(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT SUM(wp.quantity) FROM WarehouseProduct wp WHERE wp.product.id = :productId")
    Optional<Integer> findTotalQuantityByProductId(@Param("productId") Long productId);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT wp FROM WarehouseProduct wp WHERE wp.warehouse.id = :warehouseId")
    List<WarehouseProduct> findByWarehouseId(Long warehouseId);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT wp FROM WarehouseProduct wp WHERE wp.product.id = :productId ORDER BY wp.quantity DESC")
    List<WarehouseProduct> findByProductIdAndQuantity(Long productId);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<WarehouseProduct> findByWarehouseAndProduct(Warehouse warehouse, Product product);

}
