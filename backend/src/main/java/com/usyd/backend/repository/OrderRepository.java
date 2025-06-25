package com.usyd.backend.repository;

import com.usyd.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(long userId);
    List<Order> findByOrderStatus(String orderStatus);
    Order findByTrackingNumber(String trackingNumber);
}
