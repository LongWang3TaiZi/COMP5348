package com.usyd.backend.controller;

import com.usyd.backend.dto.UserDTO;
import com.usyd.backend.model.User;
import com.usyd.backend.model.request.UserLoginRequest;
import com.usyd.backend.model.request.UserRegisterRequest;
import com.usyd.backend.model.request.UserUpdateRequest;
import com.usyd.backend.model.response.BaseResponse;
import com.usyd.backend.model.response.UserResponse;
import com.usyd.backend.service.UserService;
import com.usyd.backend.utils.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/comp5348/user")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<UserResponse>> registerUser(@RequestBody UserRegisterRequest request) {
        logger.info("Received registration request for user: {}", request.getEmail());
        return userService.registerUser(request)
                .thenApply(userDTO -> {
                    if (userDTO == null) {
                        logger.warn("Registration failed for user: {}", request.getUsername());
                        UserResponse response = new UserResponse((User) null, ResponseCode.F1.getMessage(),
                                ResponseCode.F1.getResponseCode());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                    logger.info("User registered successfully: {}", request.getEmail());
                    UserResponse response = new UserResponse(userDTO, ResponseCode.A0.getMessage(),
                            ResponseCode.A0.getResponseCode());
                    return ResponseEntity.ok(response);
                });
    }

    @PutMapping("/update")
    public CompletableFuture<ResponseEntity<BaseResponse>> updateUser(@RequestBody UserUpdateRequest request) {
        logger.info("Received update request for user: {}", request.getUser().getEmail());

        return CompletableFuture.supplyAsync(() -> {
            try {
                UserDTO userDTO = userService.updateUser(request);
                if (userDTO == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new UserResponse((User) null, ResponseCode.F4.getMessage(),
                                    ResponseCode.F4.getResponseCode()));
                }
                UserResponse response = new UserResponse(userDTO, ResponseCode.A4.getMessage(), ResponseCode.A4.getResponseCode());
                return ResponseEntity.ok(response);
            }  catch (IllegalArgumentException | NoSuchAlgorithmException e) {
                logger.error("Invalid input: {}", e.getMessage());
                BaseResponse response = new BaseResponse(ResponseCode.F4.getMessage(), ResponseCode.F4.getResponseCode());
                return ResponseEntity.badRequest().body(response);
            }
        });
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody UserLoginRequest request) {
        logger.info("Received login request for user: {}", request.getEmailOrUsername());
        UserDTO userDTO = userService.loginUser(request);
        if (userDTO == null) {
            logger.warn("login failed for user: {}.", request.getEmailOrUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponse((User) null, ResponseCode.F3.getMessage(),
                            ResponseCode.F3.getResponseCode()));
        }
        logger.info("User login successfully: {}", request.getEmailOrUsername());
        UserResponse response = new UserResponse(userDTO, ResponseCode.A1.getMessage(),
                ResponseCode.A1.getResponseCode());
        return ResponseEntity.ok(response);
    }
}
