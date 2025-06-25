package com.usyd.backend.model.response;


import com.usyd.backend.dto.OrderDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponse extends BaseResponse {
    private OrderDTO order;
    private List<OrderDTO> orders;

    public OrderResponse(OrderDTO order, String message, String responseCode) {
        super(message, responseCode);
        this.order = order;
    }

    public OrderResponse(List<OrderDTO> orders, String message, String responseCode) {
        super(message, responseCode);
        this.orders = orders;
    }

    public OrderResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}
