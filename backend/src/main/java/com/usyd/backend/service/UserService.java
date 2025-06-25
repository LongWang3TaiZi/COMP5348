package com.usyd.backend.service;

import com.usyd.backend.dto.UserDTO;
import com.usyd.backend.model.request.UserLoginRequest;
import com.usyd.backend.model.request.UserRegisterRequest;
import com.usyd.backend.model.request.UserUpdateRequest;
import com.usyd.backend.model.response.UserResponse;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<UserDTO> registerUser(UserRegisterRequest request);
    UserDTO loginUser(UserLoginRequest request);
    UserDTO updateUser(UserUpdateRequest request) throws NoSuchAlgorithmException;
}
