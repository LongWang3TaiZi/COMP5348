package com.usyd.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usyd.backend.configs.RabbitMQConfig;
import com.usyd.backend.model.externalRequest.ExternalEmailRequest;
import com.usyd.backend.model.externalResponse.ExternalEmailResponse;
import com.usyd.backend.utils.enums.ServiceNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 1000;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExternalService externalService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    @Retryable(value = {Exception.class}, maxAttempts = MAX_RETRIES, backoff = @Backoff(delay = RETRY_DELAY))
    public void processEmailRequest(String message) {
        logger.info("Received email request: {}", message);
        try {
            ExternalEmailRequest request = objectMapper.readValue(message, ExternalEmailRequest.class);
            CompletableFuture<ExternalEmailResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.EMAIL,
                    "/send",
                    HttpMethod.POST,
                    request,
                    ExternalEmailResponse.class
            );

            ExternalEmailResponse response = futureResponse.get();
            if (response != null && "SEND".equals(response.getSendingStatus().toUpperCase())) {
                logger.info("Email sent successfully for request: {}", request);
            } else {
                logger.warn("Failed to send email, moving to dead letter queue. Request: {}", request);
                sendToDeadLetterQueue(message);
            }
        } catch (Exception e) {
            logger.error("Error processing email request: {}", message, e);
            sendToDeadLetterQueue(message);
        }
    }

    private void sendToDeadLetterQueue(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_DEAD_LETTER_EXCHANGE,
                RabbitMQConfig.EMAIL_DEAD_LETTER_ROUTING_KEY,
                message);
        logger.info("Message sent to dead letter queue: {}", message);
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_DEAD_LETTER_QUEUE)
    public void processDeadLetterEmailRequest(String message) {
        logger.warn("Processing dead letter queue message: {}", message);
        try {
            ExternalEmailRequest request = objectMapper.readValue(message, ExternalEmailRequest.class);

            // implement retry logic here
            boolean retrySuccess = retryEmailSend(request);

            if (retrySuccess) {
                logger.info("Successfully processed dead letter message: {}", request);
            } else {
                logger.error("Failed to process dead letter message after retries: {}", request);
            }
        } catch (Exception e) {
            logger.error("Error processing dead letter queue message: {}", message, e);
        }
    }

    @Retryable(value = {Exception.class}, maxAttempts = MAX_RETRIES, backoff = @Backoff(delay = RETRY_DELAY))
    private boolean retryEmailSend(ExternalEmailRequest request) throws Exception {
        CompletableFuture<ExternalEmailResponse> futureResponse = externalService.sendRequest(
                ServiceNameEnum.EMAIL,
                "/send",
                HttpMethod.POST,
                request,
                ExternalEmailResponse.class
        );

        ExternalEmailResponse response = futureResponse.get();
        return response != null && "SEND".equals(response.getSendingStatus().toUpperCase());
    }
}