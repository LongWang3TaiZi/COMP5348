package com.usyd.backend.model.response;

import com.usyd.backend.dto.DeliveryInformationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse extends BaseResponse{
    private List<DeliveryInformationDTO> deliveryInformations;
    private DeliveryInformationDTO deliveryInformation;

    public DeliveryResponse(String message, String responseCode, List<DeliveryInformationDTO> deliveryInformations) {
        super(message, responseCode);
        this.deliveryInformations = deliveryInformations;
    }

    public DeliveryResponse(String message, String responseCode, DeliveryInformationDTO deliveryInformation) {
        super(message, responseCode);
        this.deliveryInformation = deliveryInformation;
    }

    public DeliveryResponse(String message, String responseCode) {
        super(message, responseCode);
    }
}
