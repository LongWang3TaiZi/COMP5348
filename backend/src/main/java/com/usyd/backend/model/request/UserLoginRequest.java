package com.usyd.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserLoginRequest {
    private String emailOrUsername;
    private String password;
}
