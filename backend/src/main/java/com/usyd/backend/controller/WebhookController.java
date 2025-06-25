package com.usyd.backend.controller;


import com.usyd.backend.model.externalRequest.WebhookRequest;
import com.usyd.backend.model.externalResponse.ProcessWebhookResponse;
import com.usyd.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comp5348/webhook")
public class WebhookController {
    @Autowired
    private OrderService orderService;
    @PostMapping("")
    public ResponseEntity<ProcessWebhookResponse> processWebhook(@RequestBody WebhookRequest request){
        ProcessWebhookResponse response = orderService.updateOrderDeliveryStatus(request);
        return ResponseEntity.ok(response);
    }
}
