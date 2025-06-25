package com.usyd.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usyd.backend.model.Address;
import com.usyd.backend.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String account;
    private String token;
    private Address address;

    public UserDTO(User userEntity){
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.username = userEntity.getUsername();
        if (userEntity.getAccount() != null) this.account = userEntity.getAccount();
        if (userEntity.getAddress() != null) this.address = userEntity.getAddress();
    }
}
