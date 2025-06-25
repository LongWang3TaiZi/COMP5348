package com.usyd.backend.service.impl;

import com.usyd.backend.dto.UserDTO;
import com.usyd.backend.model.Address;
import com.usyd.backend.model.User;
import com.usyd.backend.model.externalRequest.ExternalCreateAccountRequest;
import com.usyd.backend.model.externalResponse.ExternalCreateAccountResponse;
import com.usyd.backend.model.externalResponse.ExternalEmailResponse;
import com.usyd.backend.model.request.UserLoginRequest;
import com.usyd.backend.model.request.UserRegisterRequest;
import com.usyd.backend.model.request.UserUpdateRequest;
import com.usyd.backend.repository.AddressRepository;
import com.usyd.backend.repository.UserRepository;
import com.usyd.backend.service.ExternalService;
import com.usyd.backend.service.UserService;
import com.usyd.backend.utils.JwtUtil;
import com.usyd.backend.utils.ResponseCode;
import com.usyd.backend.utils.Utils;
import com.usyd.backend.utils.enums.ServiceNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private ExternalService externalService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AddressRepository addressRepository,
                           ExternalService externalService, JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.externalService = externalService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Async
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CompletableFuture<UserDTO> registerUser(UserRegisterRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Registering user with email: {}", request.getEmail());

            Optional<User> existingUserByEmail = userRepository.findUserByEmail(request.getEmail());
            if (existingUserByEmail.isPresent()) {
                logger.warn("Registration failed: email {} already exists.", request.getEmail());
                return null;
            }

            Optional<User> existingUserByUsername = userRepository.findUserByUsername(request.getUsername());
            if (existingUserByUsername.isPresent()) {
                logger.warn("Registration failed: Username {} already exists.", request.getEmail());
                return null;
            }

            try {
                String accountNumber = createAccount(request);
                if (accountNumber == null) return null;
                String hashPassword = Utils.encodePassword(request.getPassword());
                User user = new User(request.getEmail(), request.getUsername(), hashPassword);
                user.setAccount(accountNumber);
                User savedUser = userRepository.save(user);
                logger.info("User registered successfully: {}", savedUser);
                UserDTO userDTO = new UserDTO(savedUser);
                return userDTO;
            } catch (NoSuchAlgorithmException ex) {
                logger.error("Error while encoding password for email: {}", request.getEmail(), ex);
                return null;
            }
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDTO loginUser(UserLoginRequest request) {
        logger.info("Login user with: {}", request.getEmailOrUsername());
        if (userRepository.findUserByEmail(request.getEmailOrUsername()) == null){
            logger.warn("Login failed: {} not exists.", request.getEmailOrUsername());
            return null;
        }
        // checking user password
        try {
            String hashPassword = Utils.encodePassword(request.getPassword());
            Optional<User> user;
            if(request.getEmailOrUsername().contains("@")){
                user =  userRepository.findUserByEmail(request.getEmailOrUsername());
            } else {
                user =  userRepository.findUserByUsername(request.getEmailOrUsername());
            }
            if(user.isPresent() && user.get().getPassword().equals(hashPassword)){
                logger.info("User login successfully: {}", user);
                // generating the JWT token
                String token = jwtUtil.generateToken(user.get().getEmail());
                UserDTO userDTO = new UserDTO(user.get());
                userDTO.setToken(token);
                return userDTO;
            }

            return null;
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Error while encoding password for: {}", request.getEmailOrUsername(), ex);
            return null;
        }
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserUpdateRequest request) throws NoSuchAlgorithmException {
        logger.info("Update user with: {}", request.getUser().getEmail());

        Optional<User> optionalUser = userRepository.findUserByEmail(request.getUser().getEmail());
        User existingUser;
        if(optionalUser.isPresent()){
            existingUser = optionalUser.get();
        } else {
            logger.warn("Login failed: {} not exists.", request.getUser().getEmail());
            return null;
        }

        if (request.getUser().getPassword() != null && !request.getUser().getPassword().isEmpty()) {
            existingUser.setPassword(Utils.encodePassword(request.getUser().getPassword()));
        }

        Address address = existingUser.getAddress();

        if (address == null) {
            address = new Address();
        }

        address.setCountry(request.getAddress().getCountry());
        address.setState(request.getAddress().getState());
        address.setCity(request.getAddress().getCity());
        address.setPostCode(request.getAddress().getPostCode());
        address.setAddress(request.getAddress().getAddress());

        address = addressRepository.save(address);
        existingUser.setAddress(address);

        User updatedUser = userRepository.save(existingUser);

        return new UserDTO(updatedUser);
    }


    private String createAccount(UserRegisterRequest request){
        ExternalCreateAccountRequest createAccountRequest =
                new ExternalCreateAccountRequest(request.getUsername(), request.getEmail());
        try {
            CompletableFuture<ExternalCreateAccountResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.BANK,
                    "/account/create",
                    HttpMethod.POST,
                    createAccountRequest,
                    ExternalCreateAccountResponse.class
            );
            ExternalCreateAccountResponse response = futureResponse.get();
            if (response == null) return null;
            if (response.getResult() != null && response.getResult().getResponseCode().equals("A0")) {
                return response.getAccount().getAccountNumber();
            } else {
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to create account", e);
            return null;
        }
    }
}
