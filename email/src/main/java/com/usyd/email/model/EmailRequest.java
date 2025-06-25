package com.usyd.email.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    private String emailAddress;
    private String message;
}
