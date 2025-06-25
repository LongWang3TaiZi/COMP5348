package com.usyd.backend.service.impl;

import com.usyd.backend.dto.WarehouseDTO;
import com.usyd.backend.model.request.WarehouseRequest;
import com.usyd.backend.model.externalRequest.ExternalWarehouseRequest;
import com.usyd.backend.model.externalResponse.ExternalWarehouseResponse;
import com.usyd.backend.service.ExternalService;
import com.usyd.backend.service.WarehouseService;
import com.usyd.backend.utils.ResponseCode;
import com.usyd.backend.utils.enums.ServiceNameEnum;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    @Autowired
    private ExternalService externalService;

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        try {
            CompletableFuture<ExternalWarehouseResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "",
                    HttpMethod.GET,
                    null,
                    ExternalWarehouseResponse.class
            );
            ExternalWarehouseResponse response = futureResponse.get();
            if (response != null) {
                return response.getWarehouses();
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(Long id) {
        try {
            CompletableFuture<ExternalWarehouseResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "/" + id,
                    HttpMethod.GET,
                    null,
                    ExternalWarehouseResponse.class
            );
            ExternalWarehouseResponse response = futureResponse.get();
            if (response != null) {
                return response.getResponse();
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }

    @Override
    public WarehouseDTO createWarehouse(ExternalWarehouseRequest request) {
        try {
            CompletableFuture<ExternalWarehouseResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "",
                    HttpMethod.POST,
                    request,
                    ExternalWarehouseResponse.class
            );
            ExternalWarehouseResponse createWarehouseResponse = futureResponse.get();
            if (createWarehouseResponse != null && createWarehouseResponse.getResult() != null
                    && createWarehouseResponse.getResult().getResponseCode().equals(ResponseCode.W0.toString())) {

                return createWarehouseResponse.getResponse();
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }


    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseRequest warehouseRequestDetails) throws OptimisticLockException {
        try {
            CompletableFuture<ExternalWarehouseResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "/" + id,
                    HttpMethod.PUT,
                    warehouseRequestDetails,
                    ExternalWarehouseResponse.class
            );
            ExternalWarehouseResponse response = futureResponse.get();
            if (response != null) {
                return response.getResponse();
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return null;
        }
    }

    @Override
    public Boolean deleteWarehouse(Long id) throws OptimisticLockException {
        try {
            CompletableFuture<ExternalWarehouseResponse> futureResponse = externalService.sendRequest(
                    ServiceNameEnum.WAREHOUSE,
                    "/" + id,
                    HttpMethod.DELETE,
                    null,
                    ExternalWarehouseResponse.class
            );
            ExternalWarehouseResponse response = futureResponse.get();
            if (response != null) {
                return response.getResult().getResponseCode().equals(ResponseCode.W0.toString());
            }
            return false;
        } catch (Exception e) {
            logger.warn("Error occoured: {}", e);
            return false;
        }
    }
}