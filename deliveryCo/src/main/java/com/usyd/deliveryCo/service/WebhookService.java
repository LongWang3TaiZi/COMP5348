package com.usyd.deliveryCo.service;

import com.usyd.deliveryCo.model.WebhookMessage;
import com.usyd.deliveryCo.model.webhookRequest.WebhookRequest;
import com.usyd.deliveryCo.model.webhookResponse.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String DEAD_LETTER_QUEUE = "deadLetterQueue";

    // send webhook
    public boolean sendWebhook(WebhookMessage request) {
        sendRequest(request.getUrl(), request.getWebhookRequest());
        return true;
    }

    // send the webhook request 
    @Async
    public CompletableFuture<WebhookResponse> sendRequest(String url, WebhookRequest request) {
    return CompletableFuture.supplyAsync(() -> {
        HttpHeaders headers = createHeaders();
        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

        logger.info("send webhook request to {}", url);

        try {
            // send the request and get the response
            ResponseEntity<WebhookResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, WebhookResponse.class);
            WebhookResponse response = responseEntity.getBody();
            logger.info("send webhook request to {} successfully", url);

            // check if the response acknowledgement is false
            if (response != null && !response.isAck()) {
                WebhookMessage deadLetterMessage = new WebhookMessage(request, url);
                sendToDeadLetterQueue(deadLetterMessage);
                logger.warn("Webhook response ack is false, message sent to dead letter queue");
            }

            return response;
        } catch (Exception e) {
             // log the error and send the failed message to the dead letter queue
            logger.error("send webhook request to {} failed: {}", url, e.getMessage());
            WebhookMessage deadLetterMessage = new WebhookMessage(request, url);
            sendToDeadLetterQueue(deadLetterMessage);
            logger.warn("send failed, message sent to dead letter queue");
            throw new RuntimeException("send webhook request failed", e);
        }
        });
    }

    // Sends a message to the dead letter queue.
    private void sendToDeadLetterQueue(WebhookMessage message) {
        logger.info("message sent to dead letter queue: {}", message);
        rabbitTemplate.convertAndSend(DEAD_LETTER_QUEUE, message);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // TODO: add headers for each service if required
        return headers;
    }
}