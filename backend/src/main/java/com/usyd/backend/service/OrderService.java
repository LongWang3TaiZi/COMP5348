package com.usyd.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.usyd.backend.dto.DeliveryInformationDTO;
import com.usyd.backend.dto.OrderDTO;
import com.usyd.backend.model.externalRequest.WebhookRequest;
import com.usyd.backend.model.externalResponse.ProcessWebhookResponse;
import com.usyd.backend.model.request.OrderRequest;
import com.usyd.backend.model.response.BaseResponse;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderRequest request);

    List<OrderDTO> getAllOrderByCustomerId(long customerId);
    OrderDTO getOrderById(long Id);

    boolean dispatchOrder();

    boolean dispatchById(long id);

    ProcessWebhookResponse updateOrderDeliveryStatus(WebhookRequest request);

    OrderDTO payOrder(long id);

    boolean cancelOrder(long id) throws JsonProcessingException;

    DeliveryInformationDTO getDeliveryInforById(long orderId);

    List<DeliveryInformationDTO> getDeliveriesByUserEmail(String userEmail);

}
