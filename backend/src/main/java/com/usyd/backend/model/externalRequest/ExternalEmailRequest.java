package com.usyd.backend.model.externalRequest;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExternalEmailRequest {
    private String emailAddress;
    private String message;
}
