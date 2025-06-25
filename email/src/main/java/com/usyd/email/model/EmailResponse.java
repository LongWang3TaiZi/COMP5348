package com.usyd.email.model;

import com.usyd.email.utils.SendingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailResponse {
    private SendingStatus sendingStatus;

    public EmailResponse(SendingStatus sendingStatus) {
        this.sendingStatus = sendingStatus;
    }
}
