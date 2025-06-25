package com.usyd.deliveryCo.service.messaging;

import com.usyd.deliveryCo.model.DeliveryOrder;
import com.usyd.deliveryCo.model.WebhookMessage;
import com.usyd.deliveryCo.model.webhookRequest.WebhookRequest;
import com.usyd.deliveryCo.repository.DeliveryOrderRepository;
import com.usyd.deliveryCo.service.WebhookService;
import com.usyd.deliveryCo.utils.DeliveryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Random;

@Service
public class DeliveryStatusUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryStatusUpdateService.class);

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    private final Random random = new Random();

    private static final int BASE_WAIT_TIME = 5000; // default waiting time 5 seconds
    private static final double BASE_LOSS_RATE = 0.05; // default loss rate 5%

    // process delivery 
    @RabbitListener(queues = "deliveryCOQueue")
    public void processDelivery(Long deliveryOrderId) {
        logger.info("Received message for delivery order ID: {}", deliveryOrderId);
        // get delivery order
        DeliveryOrder order = deliveryOrderRepository.findById(deliveryOrderId).orElse(null);
        if (order == null) return;

        // wait for a random time
        try {
            int waitTime = calculateWaitTime();
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // update delivery status
        DeliveryStatus oldStatus = order.getDeliveryStatus();
        // if package lost, set delivery status to LOST
        if (isPackageLost()) {
            logger.info("Package lost for delivery order ID: {}", deliveryOrderId);
            order.setDeliveryStatus(DeliveryStatus.LOST);
        } else {
            // update delivery status
            updateDeliveryStatus(order);
            logger.info("Updated status for delivery order ID: {} to {}", deliveryOrderId, order.getDeliveryStatus());
        }

        DeliveryOrder savedOrder = deliveryOrderRepository.save(order);
        // if delivery status changed, send webhook request
        if (savedOrder.getDeliveryStatus() != oldStatus) {
            WebhookRequest webhookRequest = new WebhookRequest(order);
            WebhookMessage message = new WebhookMessage(webhookRequest, savedOrder.getWebhookUrl());
            rabbitTemplate.convertAndSend("webhookQueue", message);
            logger.info("Sent webhook request to queue for delivery order ID: {}", deliveryOrderId);
        }

        // if should continue processing, send message back to queue
        if (shouldContinueProcessing(order)) {
            rabbitTemplate.convertAndSend("deliveryCOQueue", order.getId());
            logger.info("Sent message back to queue for delivery order ID: {}", deliveryOrderId);
        }
    }

    // calculate wait time
    private int calculateWaitTime() {
        // calculate fluctuation
        double fluctuation = (random.nextDouble() * 0.2) - 0.1;
        return (int) (BASE_WAIT_TIME * (1 + fluctuation));
    }

    // check if package lost
    private boolean isPackageLost() {
        // calculate fluctuation
        double fluctuation = (random.nextDouble() * 0.2) - 0.1;
        // calculate adjusted loss rate
        double adjustedLossRate = BASE_LOSS_RATE * (1 + fluctuation);
        // check if lost
        return random.nextDouble() < adjustedLossRate;
    }

    // update delivery status
    private void updateDeliveryStatus(DeliveryOrder order) {
        DeliveryStatus currentStatus = order.getDeliveryStatus();
        // update delivery status
        switch (currentStatus) {
            case CREATED:
                order.setDeliveryStatus(DeliveryStatus.PICKED_UP);
                break;
            case PICKED_UP:
                order.setDeliveryStatus(DeliveryStatus.DELIVERING);
                break;
            case DELIVERING:
                order.setDeliveryStatus(DeliveryStatus.DELIVERED);
                break;
            default:
                // Do nothing for DELIVERED or LOST
                break;
        }
    }

    // check if should continue processing
    private boolean shouldContinueProcessing(DeliveryOrder order) {
        return order.getDeliveryStatus() != DeliveryStatus.DELIVERED
                && order.getDeliveryStatus() != DeliveryStatus.LOST;
    }


}