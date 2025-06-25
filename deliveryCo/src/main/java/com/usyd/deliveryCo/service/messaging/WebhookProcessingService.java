package com.usyd.deliveryCo.service.messaging;

import com.usyd.deliveryCo.model.WebhookMessage;
import com.usyd.deliveryCo.model.webhookRequest.WebhookRequest;
import com.usyd.deliveryCo.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebhookProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookProcessingService.class);

    @Autowired
    private WebhookService webhookService;

    // process webhook
    @RabbitListener(queues = "webhookQueue")
    public void processWebhook(WebhookMessage message) {
        logger.info("Processing webhook request for delivery order ID: {}", message.getWebhookRequest().getDeliveryOrderId());
        webhookService.sendWebhook(message);
    }
}