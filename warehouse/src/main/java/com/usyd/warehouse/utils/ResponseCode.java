package com.usyd.warehouse.utils;

import lombok.Getter;

@Getter
public enum ResponseCode {
    W0("Warehouse created successfully", "W0"),
    W1("Warehouse updated successfully", "W1"),
    W2("Warehouse deleted successfully", "W2"),
    W3("Warehouse not found", "W3"),
    W4("Failed to create warehouse", "W4"),
    W5("Failed to update warehouse", "W5"),
    W6("Failed to delete warehouse", "W6"),
    W7("Warehouse retrieve successfully", "W7"),
    W8("Not enough stock available to fulfill the order", "W8"),
    W9("Warehouse product updated successfully", "W9"),
    W10("Failed to update warehouse product", "W10"),
    P0("Product created successfully", "P0"),
    P1("Product updated successfully", "P1"),
    P2("Product deleted successfully", "P2"),
    P3("Product not found", "P3"),
    P4("Failed to create product", "P4"),
    P5("Failed to update product", "P5"),
    P6("Failed to delete product", "P6"),
    P7("Product retrieve successfully", "P7"),
    A2("Product assigned to warehouse successfully", "A2"),
    A3("Failed to assign product to warehouse", "A3");

    private final String message;
    private final String responseCode;

    ResponseCode(String message, String responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }
}
