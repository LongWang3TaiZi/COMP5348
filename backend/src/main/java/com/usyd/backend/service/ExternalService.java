package com.usyd.backend.service;

import com.usyd.backend.utils.enums.ServiceNameEnum;
import org.springframework.http.HttpMethod;

import java.util.concurrent.CompletableFuture;

public interface ExternalService {
    <T, R> CompletableFuture<R> sendRequest(ServiceNameEnum serviceName, String endpoint, HttpMethod method, T request, Class<R> responseType);
}
