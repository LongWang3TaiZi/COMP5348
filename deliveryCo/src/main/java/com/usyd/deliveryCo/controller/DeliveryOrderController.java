package com.usyd.deliveryCo.controller;

import com.usyd.deliveryCo.dto.DeliveryOrderDTO;
import com.usyd.deliveryCo.model.request.DeliveryRequest;
import com.usyd.deliveryCo.service.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comp5348/deliveryCo")
public class DeliveryOrderController {
    private final DeliveryOrderService deliveryOrderService;

    @Autowired
    public DeliveryOrderController(DeliveryOrderService deliveryOrderService) {
        this.deliveryOrderService = deliveryOrderService;
    }

    // create delivery order
    @PostMapping("/create")
    public ResponseEntity<DeliveryOrderDTO> createDeliveryOrder(
            @RequestBody DeliveryRequest request) {
        DeliveryOrderDTO deliveryOrder = deliveryOrderService
                .createOrder(request);
        if (deliveryOrder != null) {
            return ResponseEntity.ok(deliveryOrder);
        }
        return ResponseEntity.badRequest().build();

    }

    // create delivery order batch
    @PostMapping("/create/batch")
    public ResponseEntity<List<DeliveryOrderDTO>> createDeliveryOrderBatch(
            @RequestBody List<DeliveryRequest> request) {
        List<DeliveryOrderDTO> deliveryOrder = deliveryOrderService
                .createOrderBatch(request);
        if (deliveryOrder != null) {
            return ResponseEntity.ok(deliveryOrder);
        }
        return ResponseEntity.badRequest().build();

    }

    // get delivery order
    @GetMapping("/{deliveryOrderId}")
    public ResponseEntity<DeliveryOrderDTO> getDeliveryOrder(
            @PathVariable Long deliveryOrderId) {
        DeliveryOrderDTO deliveryOrder = deliveryOrderService.getDeliveryOrder(deliveryOrderId);
        if (deliveryOrder == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(deliveryOrder);
    }

    // get all delivery order
    @GetMapping("/all/{email}")
    public ResponseEntity<List<DeliveryOrderDTO>> getAllDeliveryOrder(
            @PathVariable String email) {
        List<DeliveryOrderDTO> deliveryOrder = deliveryOrderService.getAllDeliveryOrder(email);
        if (deliveryOrder == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(deliveryOrder);
    }
}
