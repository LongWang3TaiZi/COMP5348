package com.usyd.deliveryCo.repository;

import com.usyd.deliveryCo.model.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Data Access Object for delivery order database table.
 */
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
    Collection<DeliveryOrder> findByEmail(String email);
}
