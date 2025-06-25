package com.usyd.backend.service.impl;

import com.usyd.backend.service.ExternalService;
import com.usyd.backend.utils.Constants;
import com.usyd.backend.utils.enums.ServiceNameEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ExternalServiceImpl implements ExternalService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    private final Map<ServiceNameEnum, String> serviceUrls = new HashMap<>();

    public ExternalServiceImpl() {
        // TODO: replace below with actual service URLs,
        serviceUrls.put(ServiceNameEnum.BANK, Constants.BANK_BASE_URL);
        serviceUrls.put(ServiceNameEnum.EMAIL, Constants.EMAIL_BASE_URL);
        serviceUrls.put(ServiceNameEnum.DELIVERY, Constants.DELIVERY_BASE_URL);
        serviceUrls.put(ServiceNameEnum.WAREHOUSE, Constants.WAREHOUSE_BASE_URL);
        serviceUrls.put(ServiceNameEnum.PRODUCT, Constants.PRODUCT_BASE_URL);
    }

    @Override
    @Async
    public <T, R> CompletableFuture<R> sendRequest(ServiceNameEnum serviceName, String endpoint, HttpMethod method, T request, Class<R> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            // build the full URL for the request
            String url = buildUrl(serviceName, endpoint);
            // create headers for the request
            HttpHeaders headers = createHeaders(serviceName);
            // create an HTTP entity with the request body and headers
            HttpEntity<T> entity = new HttpEntity<>(request, headers);

            logger.info("Sending {} request to {}: {}", method, url, request);

            try {
                // send request and get response
                ResponseEntity<R> response = restTemplate.exchange(url, method, entity, responseType);
                logger.info("Received response from {}: {}", url, response.getBody());
                // return the response body
                return response.getBody();
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    logger.warn("Received 400 Bad Request from {}: {}", url, e.getResponseBodyAsString());
                    return null;
                } else {
                    logger.error("Error while sending {} request to {}: {}", method, url, e.getMessage());
                    throw new RuntimeException("Failed to send request to " + serviceName, e);
                }
            } catch (Exception e) {
                logger.error("Error while sending {} request to {}: {}", method, url, e.getMessage());
                throw new RuntimeException("Failed to send request to " + serviceName, e);
            }
        });
    }

    // helper method to build the full URL
    private String buildUrl(ServiceNameEnum serviceName, String endpoint) {
        String baseUrl = serviceUrls.get(serviceName);
        if (baseUrl == null) {
            throw new IllegalArgumentException("Unknown service: " + serviceName);
        }
        return baseUrl + endpoint;
    }

    // helper method to create headers for each service
    private HttpHeaders createHeaders(ServiceNameEnum serviceName) {
        HttpHeaders headers = new HttpHeaders();
        // TODO: add headers for each service if required
        return headers;
    }
}
