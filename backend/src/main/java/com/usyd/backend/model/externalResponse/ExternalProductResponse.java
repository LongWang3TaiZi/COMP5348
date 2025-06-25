package com.usyd.backend.model.externalResponse;

import com.usyd.backend.dto.ProductDTO;
import com.usyd.backend.model.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExternalProductResponse extends BaseResponse {
    private List<ProductDTO> products;
    private ProductDTO product;

    public ExternalProductResponse(String message, String responseCode) {
        super();
    }

    public ExternalProductResponse() {
        super();
    }
}
