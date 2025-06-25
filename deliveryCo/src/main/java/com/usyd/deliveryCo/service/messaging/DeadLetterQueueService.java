package com.usyd.deliveryCo.service.messaging;

import com.usyd.deliveryCo.model.WebhookMessage;
import com.usyd.deliveryCo.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueService {
    private static final Logger logger = LoggerFactory.getLogger(DeadLetterQueueService.class);

    @Autowired
    private WebhookService webhookService;

    // process dead letter
    @RabbitListener(queues = "deadLetterQueue")
    public void processDeadLetter(WebhookMessage message) {
        logger.info("process message in dead letter queue: {}", message);
        try {
            webhookService.sendRequest(message.getUrl(), message.getWebhookRequest());
            logger.info("resend webhook request successfully");
        } catch (Exception e) {
            logger.error("resend webhook request failed: {}", e.getMessage());
        }
    }
}
