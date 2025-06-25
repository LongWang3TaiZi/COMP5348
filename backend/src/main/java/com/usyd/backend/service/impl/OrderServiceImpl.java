package com.usyd.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usyd.backend.configs.RabbitMQConfig;
import com.usyd.backend.dto.DeliveryInformationDTO;
import com.usyd.backend.dto.OrderDTO;
import com.usyd.backend.dto.WarehouseDTO;
import com.usyd.backend.model.Order;
import com.usyd.backend.model.Product;
import com.usyd.backend.model.User;
import com.usyd.backend.model.externalRequest.*;
import com.usyd.backend.model.externalResponse.*;
import com.usyd.backend.model.request.OrderRequest;
import com.usyd.backend.model.response.BaseResponse;
import com.usyd.backend.repository.OrderRepository;
import com.usyd.backend.repository.ProductRepository;
import com.usyd.backend.repository.UserRepository;
import com.usyd.backend.service.ExternalService;
import com.usyd.backend.service.OrderService;
import com.usyd.backend.utils.ResponseCode;
import com.usyd.backend.utils.Utils;
import com.usyd.backend.utils.enums.DeliveryStatus;
import com.usyd.backend.utils.enums.OrderStatus;
import com.usyd.backend.utils.enums.PaymentStatus;
import com.usyd.backend.utils.enums.ServiceNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ExternalService externalService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${comp5348.project.admin.account}")
    private String adminAccount;

    @Override
    public OrderDTO createOrder(OrderRequest request) {
        Order order = new Order(request);
        Optional<User> user = userRepository.findById(request.getUserId());
        Optional<Product> product = productRepository.findById(request.getProductId());
        if (!user.isPresent() || !product.isPresent()) {
            sendOrderEmailNotification(order, false);
            return null;
        }
        order.setProduct(product.get());
        order.setUser(user.get());
        try {
            CompletableFuture<ExternalWarehouseResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "/available/" + product.get().getWarehouseProductId() + "/" + request.getQuantity(),
                    HttpMethod.GET,
                    null,
                    ExternalWarehouseResponse.class
            );
            ExternalWarehouseResponse response = futureResponse.get();
            if (response == null || response.getResult() == null || !response.getResult().getResponseCode().equals("W7")) {
                sendOrderEmailNotification(order, false);
                return null;
            }
            List<String> locations = new ArrayList<>();
            List<Long> inventoryTransactionIds = response.getResponse().getInventoryTransactionIds();
            locations = response.getResponse().getWarehouses().stream()
                    .map(WarehouseDTO::getLocation)
                    .toList();

            order.setAvailableWarehouse(locations);
            order.setInventoryTransactionIds(inventoryTransactionIds);
            Order savedOrder = orderRepository.save(order);
            if (savedOrder == null) {
                sendOrderEmailNotification(order, false);
                return null;
            }
            sendOrderEmailNotification(order, true);
            return new OrderDTO(savedOrder);
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            sendOrderEmailNotification(order, false);
            return null;
        }
    }

    @Override
    public List<OrderDTO> getAllOrderByCustomerId(long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(long Id) {
        Optional<Order> order = orderRepository.findById(Id);
        if (!order.isPresent()) return null;
        return new OrderDTO(order.get());
    }

    @Override
    public boolean dispatchOrder() {
        List<Order> orders = orderRepository.findByOrderStatus(OrderStatus.PENDING_ON_DELIVERY.toString());
        if (orders != null) {

        }
        return true;
    }

    @Override
    public boolean dispatchById(long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (!order.isPresent()) return false;
        if (!order.get().getOrderStatus().equals(OrderStatus.PENDING_ON_DELIVERY)) return false;
        ExternalDeliveryRequest request = new ExternalDeliveryRequest(order.get());
        try {
            CompletableFuture<ExternalDeliveryResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.DELIVERY,
                    "/create",
                    HttpMethod.POST,
                    request,
                    ExternalDeliveryResponse.class
            );
            ExternalDeliveryResponse response = futureResponse.get();
            if (response == null) return false;
            Order updateOrder = order.get();
            updateOrder.setOrderStatus(OrderStatus.PROCESSING_DELIVERY);
            updateOrder.setTrackingNumber(String.valueOf(response.getId()));
            Order updatedOrder = orderRepository.save(updateOrder);
            if (updatedOrder == null) return false;
            return true;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return false;
        }
    }

    @Override
    public ProcessWebhookResponse updateOrderDeliveryStatus(WebhookRequest request) {
        Order order = orderRepository.findByTrackingNumber(String.valueOf(request.getDeliveryOrderId()));
        if (order != null) {
            order.setDeliverStatus(request.getDeliveryStatus());
            switch (request.getDeliveryStatus()){
                case LOST -> order.setOrderStatus(OrderStatus.FAILED);
                case DELIVERED -> order.setOrderStatus(OrderStatus.COMPLETED);
            }
            order.setUpdateTime(LocalDateTime.now());
            sendEmailNotification(order, request.getDeliveryStatus());
            orderRepository.save(order);
            return new ProcessWebhookResponse(true);
        }
        return new ProcessWebhookResponse(false);
    }

    @Override
    public OrderDTO payOrder(long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            sendOrderPaymentEmailNotification(orderOpt.get(), false);
            return null;
        }

        Order order = orderOpt.get();
        String userAccount = order.getUser().getAccount();
        Double amount = order.getProduct().getPrice().doubleValue() * order.getQuantity();
        ExternalCreateTransactionRequest request = new ExternalCreateTransactionRequest(amount, userAccount, adminAccount);

        ExternalCreateTransactionResponse response = createTransaction(request);
        if (response == null || response.getResult().getResponseCode().equals("T2")) {
            sendOrderPaymentEmailNotification(order, false);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setPaymentStatus("InsufficientBalance");
            return orderDTO;
        }

        if (response.getTransaction().getTransactionStatus().equals(PaymentStatus.Accepted.toString())) {
            order.setOrderStatus(OrderStatus.PENDING_ON_DELIVERY);
            order.setPaymentStatus(PaymentStatus.Accepted.toString());
            order.setUpdateTime(LocalDateTime.now());
        } else if (response.getTransaction().getTransactionStatus().equals(PaymentStatus.InsufficientBalance.toString())) {
            order.setUpdateTime(LocalDateTime.now());
            order.setPaymentStatus(PaymentStatus.InsufficientBalance.toString());
        } else if (response.getTransaction().getTransactionStatus().equals(PaymentStatus.Failed.toString())) {
            order.setUpdateTime(LocalDateTime.now());
            order.setPaymentStatus(PaymentStatus.Failed.toString());
        }
        order.setTransactionId(response.getTransaction().getTransactionId());
        Order savedOrder = orderRepository.save(order);
        sendOrderPaymentEmailNotification(order, true);
        return new OrderDTO(savedOrder);
    }

    @Override
    public boolean cancelOrder(long id) throws JsonProcessingException {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) return false;

        Order order = orderOpt.get();
        ExternalRefundTransactionResponse response = refundTransaction(order.getTransactionId());
        ExternalEmailRequest emailRequest = new ExternalEmailRequest(
                order.getUser().getEmail(),
                Utils.formRefundEmailMessage(order.getUser().getEmail(), order.getId(), false)
        );
        if (response == null || response.getResult().getResponseCode().equals("T4")) {
            String message = new ObjectMapper().writeValueAsString(emailRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, message);
            return false;
        } else {
            emailRequest.setMessage(Utils.formRefundEmailMessage(order.getUser().getEmail(),
                    order.getId(), true));
        }
        try {
            String message = new ObjectMapper().writeValueAsString(emailRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, message);
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            logger.warn("Failed to send email: {}", emailRequest);
        }

        boolean unholdResult = unholdProduct(order.getInventoryTransactionIds());
        if (!unholdResult) {
            if (chargeBack(response.getTransaction().getTransactionId()));
            return false;
        }

        // clear the inventoryTransactionIds list
        // this will automatically remove corresponding entries from order_inventory_transaction_ids table
        order.getInventoryTransactionIds().clear();

        // Update order status
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdateTime(LocalDateTime.now());

        // Save the updated order
        orderRepository.save(order);

        return true;
    }

    @Override
    public DeliveryInformationDTO getDeliveryInforById(long deliveryId) {

        try {
            CompletableFuture<ExternalDeliveryResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.DELIVERY,
                    "/" + deliveryId,
                    HttpMethod.GET,
                    null,
                    ExternalDeliveryResponse.class
            );
            ExternalDeliveryResponse response = futureResponse.get();
            if (response == null) return null;
            return new DeliveryInformationDTO(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve delivery information", e);
            return null;
        }
    }

    @Override
    public List<DeliveryInformationDTO> getDeliveriesByUserEmail(String userEmail) {
        try {
            CompletableFuture<DeliveryInformationDTO[]> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.DELIVERY,
                    "/all/" + userEmail,
                    HttpMethod.GET,
                    null,
                    DeliveryInformationDTO[].class
            );

            DeliveryInformationDTO[] responses = futureResponse.get();
            if (responses == null) {
                return Collections.emptyList();
            }

            return Arrays.asList(responses);
        } catch (Exception e) {
            logger.error("Failed to retrieve delivery information", e);
            return Collections.emptyList();
        }
    }


    private  boolean chargeBack(long transactionId) {
        try {
            CompletableFuture<ExternalRefundTransactionResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.BANK,
                    "/transaction/charge-back/" + transactionId,
                    HttpMethod.POST,
                    null,
                    ExternalRefundTransactionResponse.class
            );
            ExternalRefundTransactionResponse response = futureResponse.get();
            if (response == null || response.getResult().getResponseCode().equals("C2")) {
                logger.error("Failed to charge back transaction for transaction Id: {}", transactionId);
                return false;
            };

            return true;
        } catch (Exception e) {
            logger.error("Failed to charge back transaction", e);
            return false;
        }

    }
    private boolean unholdProduct(List<Long> inventoryTransactionIds) {
        try {
            ExternalUnholdProductRequest request = new ExternalUnholdProductRequest(inventoryTransactionIds);
            CompletableFuture<ExternalRefundTransactionResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "/unhold",
                    HttpMethod.PUT,
                    request,
                    ExternalRefundTransactionResponse.class
            );
            ExternalRefundTransactionResponse response = futureResponse.get();
            if (response == null || !response.getResult().getResponseCode().equals("W9")) return false;

            return true;
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            return false;
        }
    }

    private ExternalRefundTransactionResponse refundTransaction(long originalTransactionId){
        try {
            CompletableFuture<ExternalRefundTransactionResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.BANK,
                    "/transaction/refund/" + originalTransactionId,
                    HttpMethod.POST,
                    null,
                    ExternalRefundTransactionResponse.class
            );
            ExternalRefundTransactionResponse response = futureResponse.get();
            if (response == null || response.getResult().getResponseCode().equals("T4")) return null;

            return response;
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            return null;
        }
    }

    private ExternalCreateTransactionResponse createTransaction(ExternalCreateTransactionRequest request) {
        try {
            CompletableFuture<ExternalCreateTransactionResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.BANK,
                    "/transaction/create",
                    HttpMethod.POST,
                    request,
                    ExternalCreateTransactionResponse.class
            );
            ExternalCreateTransactionResponse response = futureResponse.get();
            if (response == null) return null;
            if (response.getResult() != null &&
                    (response.getResult().getResponseCode().equals("T0") ||
                            response.getResult().getResponseCode().equals("T2"))) {
                return response;
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to create transaction", e);
            return null;
        }
    }

    private void sendEmailNotification(Order order, DeliveryStatus deliveryStatus) {
        ExternalEmailRequest emailRequest = new ExternalEmailRequest(
            order.getUser().getEmail(),
            Utils.formDeliveryStatusEmailMessage(order.getUser().getEmail(), order.getId(), deliveryStatus)
        );
        try {
            String message = new ObjectMapper().writeValueAsString(emailRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, message);
        } catch (Exception e) {
            logger.warn("Failed to send email: {}", emailRequest);
        }
    }

    private void sendOrderEmailNotification(Order order, boolean orderStatus){
        ExternalEmailRequest emailRequest = new ExternalEmailRequest(
                order.getUser().getEmail(),
                Utils.formOrderEmailMessage(order.getUser().getEmail(), order.getId(), orderStatus)
        );
        try {
            String message = new ObjectMapper().writeValueAsString(emailRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, message);
        } catch (Exception e) {
            logger.warn("Failed to send email: {}", emailRequest);
        }
    }

    private void sendOrderPaymentEmailNotification(Order order, boolean paymentStatus){
        ExternalEmailRequest emailRequest = new ExternalEmailRequest(
                order.getUser().getEmail(),
                Utils.formOrderPaymentEmailMessage(order.getUser().getEmail(), order.getId(), paymentStatus)
        );
        try {
            String message = new ObjectMapper().writeValueAsString(emailRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, message);
        } catch (Exception e) {
            logger.warn("Failed to send email: {}", emailRequest);
        }
    }
}
