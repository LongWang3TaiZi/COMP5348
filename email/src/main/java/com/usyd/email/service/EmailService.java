package com.usyd.email.service;

import com.usyd.email.model.EmailRequest;
import com.usyd.email.model.EmailResponse;
import com.usyd.email.utils.SendingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Async
    public CompletableFuture<EmailResponse> sendEmail(EmailRequest request) {
        EmailResponse response;
        // log the email address and message
        logger.info("\nTo email address: {} \nMessage: \n{}", request.getEmailAddress(),
                request.getMessage());
        response = new EmailResponse(SendingStatus.Send);
        return CompletableFuture.completedFuture(response);
    }
}
