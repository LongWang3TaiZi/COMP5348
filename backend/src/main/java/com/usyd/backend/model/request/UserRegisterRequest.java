package com.usyd.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String username;
    private String password;
}
