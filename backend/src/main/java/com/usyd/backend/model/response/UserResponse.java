package com.usyd.backend.model.response;

import com.usyd.backend.dto.UserDTO;
import com.usyd.backend.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends BaseResponse{
    private UserDTO user;
    private String token; // filed for JWT token

    public UserResponse(User user, String message, String responseCode) {
        super(message, responseCode);
        if (user != null)  this.user = new UserDTO(user);
    }

    public UserResponse(UserDTO userDTO, String message, String responseCode) {
        super(message, responseCode);
        this.user = userDTO;
        this.token = userDTO.getToken();
    }
}
