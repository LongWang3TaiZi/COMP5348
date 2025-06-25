package com.usyd.backend.utils;


import lombok.Getter;

@Getter
public enum ResponseCode {
    F0("Internal server error", "F0"),
    // user related resposne code
    F1("User already exists", "F1"),
    F3("User login failed", "F3"),
    F4("User update failed", "F4"),
    A0("User created successfully", "A0"),
    A1("User login successfully", "A1"),
    A4("User update successfully", "A4"),
    // produce related resposne code
    P0("Product created successfully", "P0"),
    P1("Product updated successfully", "P1"),
    P2("Product deleted successfully", "P2"),
    P3("Product not found", "P3"),
    P4("Failed to create product", "P4"),
    P5("Failed to update product", "P5"),
    P6("Failed to delete product", "P6"),
    P7("Product retrieve successfully", "P7"),
    // warehouse related resposne code
    W0("WarehouseRequest created successfully", "W0"),
    W1("WarehouseRequest updated successfully", "W1"),
    W2("WarehouseRequest deleted successfully", "W2"),
    W3("WarehouseRequest not found", "W3"),
    W4("Failed to create warehouse", "W4"),
    W5("Failed to update warehouse", "W5"),
    W6("Failed to delete warehouse", "W6"),
    // assign product to warehouse resposne code
    A2("Product assigned to warehouse successfully", "A2"),
    A3("Failed to assign product to warehouse", "A3"),
    // order related code
    O0("Order created successfully", "O0"),
    O1("Failed to create order", "O1"),
    O2("Order retrieve successfully", "O2"),
    O3("Order not found", "O3"),
    O4("Orders are dispatched", "O4"),
    O5("Orders failed to dispatched", "O5"),
    O6("Order canceled successfully", "O6"),
    O7("Failed to cancel order", "O7"),
    // bank transaction related code
    T0("Transaction made successfully", "T0"),
    T1("Failed to make transaction", "T1"),
    T2("Insufficient balance in the account", "T2"),
    // delivery related code
    D1("Delivery information retrieve successfully", "D1"),
    D2("Failed to retrieve delivery information", "D2");
    private final String message;
    private final String responseCode;

    ResponseCode(String message, String responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }
}
