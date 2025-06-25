package com.usyd.warehouse.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UnholdProductRequest {
    private List<Long> inventoryTransactionIds;
}
