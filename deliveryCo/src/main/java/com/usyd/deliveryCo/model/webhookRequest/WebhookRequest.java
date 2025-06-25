package com.usyd.deliveryCo.model.webhookRequest;

import com.usyd.deliveryCo.model.DeliveryOrder;
import com.usyd.deliveryCo.utils.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class WebhookRequest implements Serializable {
    private long deliveryOrderId;
    private DeliveryStatus deliveryStatus;

    public WebhookRequest(DeliveryOrder deliveryOrderEntity) {
        this.deliveryOrderId = deliveryOrderEntity.getId();
        this.deliveryStatus = deliveryOrderEntity.getDeliveryStatus();
    }
}
