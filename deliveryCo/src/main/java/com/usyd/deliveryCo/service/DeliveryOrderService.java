package com.usyd.deliveryCo.service;

import com.usyd.deliveryCo.dto.DeliveryOrderDTO;
import com.usyd.deliveryCo.model.DeliveryOrder;
import com.usyd.deliveryCo.model.request.DeliveryRequest;
import com.usyd.deliveryCo.repository.DeliveryOrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for creating and managing delivery order.
 */
@Service
public class DeliveryOrderService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryOrderService.class);
    private final DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    public DeliveryOrderService(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    // create delivery order
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public DeliveryOrderDTO createOrder(DeliveryRequest request) {
        DeliveryOrder deliveryOrder = new DeliveryOrder(request);
        // save delivery order
        DeliveryOrder savedDeliveryOrder = deliveryOrderRepository.save(deliveryOrder);
        // if saved delivery order is not null, send message to queue
        if (savedDeliveryOrder != null) {
            amqpTemplate.convertAndSend("deliveryCOQueue", savedDeliveryOrder.getId());
            logger.info("Sent message to queue for new delivery order ID: {}", savedDeliveryOrder.getId());
            return new DeliveryOrderDTO(savedDeliveryOrder);
        }
        logger.warn("Failed to create delivery order");
        return null;

    }

    // create order with batch method
    @Transactional
    public List<DeliveryOrderDTO> createOrderBatch(List<DeliveryRequest> requests) {
        List<DeliveryOrder> deliveryOrders = new ArrayList<>();
        List<DeliveryOrderDTO> deliveryOrderDTOs = new ArrayList<>();

        // iterate the delivery request to process each request
        for (DeliveryRequest request : requests) {
            DeliveryOrder deliveryOrder = new DeliveryOrder(request);
            deliveryOrders.add(deliveryOrder);
        }
        // save all delivery request
        List<DeliveryOrder> savedDeliveryOrders = deliveryOrderRepository.saveAll(deliveryOrders);

        for (DeliveryOrder savedDeliveryOrder : savedDeliveryOrders) {
            // add to the meesage queue
            if (savedDeliveryOrder != null) {
                amqpTemplate.convertAndSend("deliveryCOQueue", savedDeliveryOrder.getId());
                logger.info("Sent message to queue for new delivery order ID: {}", savedDeliveryOrder.getId());
                deliveryOrderDTOs.add(new DeliveryOrderDTO(savedDeliveryOrder));
            } else {
                logger.warn("Failed to create delivery order");
            }
        }
        
        if (deliveryOrderDTOs.isEmpty()) {
            logger.warn("Failed to create any delivery orders");
            return null;
        }

        return deliveryOrderDTOs;
    }

    /**
     * The getDeliveryOrder method obtains a DeliveryOrder object based on
     * the corresponding deliveryOrderId and returns it to the DeliveryOrderDTO object.
     *
     * @param deliveryOrderId   the ID of the delivery order
     * @return                  DeliveryOrderDTO
     */
    @Transactional(readOnly = true)
    public DeliveryOrderDTO getDeliveryOrder(Long deliveryOrderId) {
        // Returns the DeliveryOrder object in the database, given the deliveryOrderId.
        DeliveryOrder deliveryOrder = deliveryOrderRepository.getReferenceById(deliveryOrderId);
        if (deliveryOrder == null) {
            return null;
        }
        return new DeliveryOrderDTO(deliveryOrder);
    }

    /**
     * The getAllDeliveryOrder method returns all Delivery order details for the specified customer mail.
     *
     * @param email     the email of the customer
     * @return          DeliveryOrderDTO
     */
    @Transactional(readOnly = true)
    public List<DeliveryOrderDTO> getAllDeliveryOrder(String email) {
        // Returns a collection of addresses corresponding to the mail.
        Collection<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findByEmail(email);
        if (deliveryOrders == null) {
            return null;
        }
        List<DeliveryOrderDTO> deliveryOrderDTOs = deliveryOrders.stream()
                .map(DeliveryOrderDTO::new)
                .collect(Collectors.toList());
        return deliveryOrderDTOs;
    }
}
