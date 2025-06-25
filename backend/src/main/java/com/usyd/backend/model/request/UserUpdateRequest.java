package com.usyd.backend.model.request;

import com.usyd.backend.model.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    private UserRegisterRequest user;
    private Address address;
}
