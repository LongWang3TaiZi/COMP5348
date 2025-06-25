package com.usyd.backend.model.externalRequest;

import com.usyd.backend.utils.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class WebhookRequest {
    private long deliveryOrderId;
    private DeliveryStatus deliveryStatus;
}

