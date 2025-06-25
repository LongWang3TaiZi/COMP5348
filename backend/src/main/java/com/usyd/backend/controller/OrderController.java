package com.usyd.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.usyd.backend.dto.DeliveryInformationDTO;
import com.usyd.backend.dto.OrderDTO;
import com.usyd.backend.model.Order;
import com.usyd.backend.model.externalRequest.WebhookRequest;
import com.usyd.backend.model.request.OrderRequest;
import com.usyd.backend.model.response.BaseResponse;
import com.usyd.backend.model.response.DeliveryResponse;
import com.usyd.backend.model.response.OrderResponse;
import com.usyd.backend.service.OrderService;
import com.usyd.backend.utils.ResponseCode;
import com.usyd.backend.utils.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comp5348/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request){
        OrderDTO orderDTO = orderService.createOrder(request);
        if (orderDTO != null) return ResponseEntity.ok(new OrderResponse(orderDTO,ResponseCode.O0.getMessage(),
                ResponseCode.O0.getResponseCode()));

        return ResponseEntity.ok(new OrderResponse(ResponseCode.O1.getMessage(),
                ResponseCode.O1.getResponseCode()));
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<OrderResponse> getOrders(@PathVariable long userId){
        List<OrderDTO> orders = orderService.getAllOrderByCustomerId(userId);
        return ResponseEntity.ok(new OrderResponse(orders, ResponseCode.O2.getMessage(),
                ResponseCode.O2.getResponseCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable long id){
        OrderDTO order = orderService.getOrderById(id);
        if (order == null) return ResponseEntity.ok(new OrderResponse(ResponseCode.O3.getMessage(),
                ResponseCode.O3.getResponseCode()));
        return ResponseEntity.ok(new OrderResponse(order, ResponseCode.O2.getMessage(),
                ResponseCode.O2.getResponseCode()));
    }

    @PutMapping("/dispatch")
    public ResponseEntity<BaseResponse> batchDispatch(){
        boolean result = orderService.dispatchOrder();
        if (result) return ResponseEntity.ok(new BaseResponse(ResponseCode.O4.getMessage(),
                ResponseCode.O4.getResponseCode()));
        return ResponseEntity.ok(new BaseResponse(ResponseCode.O5.getMessage(),
                ResponseCode.O5.getResponseCode()));
    }

    @PutMapping("/dispatch/{id}")
    public ResponseEntity<BaseResponse> dispatchById(@PathVariable long id){
        boolean result = orderService.dispatchById(id);
        if (result) return ResponseEntity.ok(new BaseResponse(ResponseCode.O4.getMessage(),
                ResponseCode.O4.getResponseCode()));
        return ResponseEntity.ok(new BaseResponse(ResponseCode.O5.getMessage(),
                ResponseCode.O5.getResponseCode()));
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable long orderId){
        OrderDTO result = orderService.payOrder(orderId);
        if (result == null) return ResponseEntity.ok(new OrderResponse(ResponseCode.T1.getMessage(),
                ResponseCode.T1.getResponseCode()));
        if (result.getPaymentStatus().equals(PaymentStatus.InsufficientBalance.toString())) {
            return ResponseEntity.ok(new OrderResponse(result, ResponseCode.T2.getMessage(),
                    ResponseCode.T2.getResponseCode()));
        }
        return ResponseEntity.ok(new OrderResponse(result, ResponseCode.T0.getMessage(),
                ResponseCode.T0.getResponseCode()));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable long orderId) throws JsonProcessingException {
        boolean result = orderService.cancelOrder(orderId);
        if (!result) return ResponseEntity.ok(new OrderResponse(ResponseCode.O7.getMessage(),
                ResponseCode.O7.getResponseCode()));
        return ResponseEntity.ok(new OrderResponse(ResponseCode.O6.getMessage(),
                ResponseCode.O6.getResponseCode()));
    }

    @GetMapping("/delivery/detail/{deliveryId}")
    public ResponseEntity<DeliveryResponse> getdeliveryInformationById(@PathVariable long deliveryId) {
        DeliveryInformationDTO deliveryInformationDTO = orderService.getDeliveryInforById(deliveryId);
        if (deliveryInformationDTO == null) return ResponseEntity.ok(new DeliveryResponse(ResponseCode.D2.getMessage(),
                ResponseCode.D2.getResponseCode()));

        return ResponseEntity.ok(new DeliveryResponse(ResponseCode.D1.getMessage(),
                ResponseCode.D1.getResponseCode(), deliveryInformationDTO));
    }

    @GetMapping("/delivery/{userEmail}")
    public ResponseEntity<DeliveryResponse> getAllDeliveriesByUserEmail(@PathVariable String userEmail){
        List<DeliveryInformationDTO> deliveryInformationDTOs = orderService.getDeliveriesByUserEmail(userEmail);
        if (deliveryInformationDTOs == null) return ResponseEntity.ok(new DeliveryResponse(ResponseCode.D2.getMessage(),
                ResponseCode.D2.getResponseCode()));

        return ResponseEntity.ok(new DeliveryResponse(ResponseCode.D1.getMessage(),
                ResponseCode.D1.getResponseCode(), deliveryInformationDTOs));
    }

}
