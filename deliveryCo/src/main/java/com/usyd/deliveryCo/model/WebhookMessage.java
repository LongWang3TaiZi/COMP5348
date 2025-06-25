package com.usyd.deliveryCo.model;

import com.usyd.deliveryCo.model.webhookRequest.WebhookRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class WebhookMessage implements Serializable {
    private WebhookRequest webhookRequest;
    private String url;
}