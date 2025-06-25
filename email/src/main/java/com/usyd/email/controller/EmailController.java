package com.usyd.email.controller;

import com.usyd.email.model.EmailRequest;
import com.usyd.email.model.EmailResponse;
import com.usyd.email.service.EmailService;
import com.usyd.email.utils.SendingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/comp5348/email")
public class EmailController {
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<EmailResponse>> sendEmail(@RequestBody EmailRequest request) {
        return emailService.sendEmail(request)
                .thenApply(response -> {
                    if (response.getSendingStatus() == SendingStatus.Send) {
                        return ResponseEntity.ok(response);
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                });
    }
}
