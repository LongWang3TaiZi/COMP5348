package com.usyd.backend.model.externalResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProcessWebhookResponse {
    private boolean ack;
}
