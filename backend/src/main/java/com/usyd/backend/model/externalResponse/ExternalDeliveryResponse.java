package com.usyd.backend.model.externalResponse;

import com.usyd.backend.dto.DeliveryInformationDTO;
import com.usyd.backend.dto.WarehouseDTO;
import com.usyd.backend.model.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalDeliveryResponse {
    private long id;
    private String productName;
    private int quantity;
    private String email;
    private String deliveryStatus;
    private String creationTime;
    private String updateTime;
    private String toAddress;
    private List<String> fromAddress;
}
