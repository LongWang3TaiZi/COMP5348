package com.usyd.backend.model.externalRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExternalUnholdProductRequest {
    private List<Long> inventoryTransactionIds;
}
