package com.usyd.backend.utils;

import com.usyd.backend.utils.enums.DeliveryStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {
    private static final String SECRET = "COMP5348";
    public static String encodePassword(String password) throws NoSuchAlgorithmException {
        // get a MessageDigest instance for SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // hash the secret and get the byte array
        byte[] hashBytes = digest.digest(SECRET.getBytes(StandardCharsets.UTF_8));
        // convert the byte array into a Base64 encoded string
        return Base64.getEncoder().encodeToString(hashBytes);

    }

    public static String formDeliveryStatusEmailMessage(String email, long orderId, DeliveryStatus deliveryStatus){
        return "Dear " + email + ",\n\n" +
                "Your order with ID " + orderId + " has been " + deliveryStatus.toString().toLowerCase() + ".\n\n" +
                "Thank you for your purchase!\n\n" +
                "Best regards,\n" +
                "The COMP5348 06 Group 1";
    }


    public static String formRefundEmailMessage(String email, long orderId, boolean refundStatus){
        String refundStatusString = refundStatus ? "Successfully refund" : "Failed refund";
        return "Dear " + email + ",\n\n" +
                "Your order with ID " + orderId + " has been " + refundStatusString + "canceled" + ".\n\n" +
                "Thank you for your purchase!\n\n" +
                "Best regards,\n" +
                "The COMP5348 06 Group 1";
    }

    public static String formOrderEmailMessage(String email, long orderId, boolean orderStatus){
        String orderStatusString = orderStatus ? "created successfully" : "failed to create order";
        return "Dear " + email + ",\n\n" +
                "Your order with ID " + orderId + " has been " + orderStatusString + ".\n\n" +
                "Thank you for your purchase!\n\n" +
                "Best regards,\n" +
                "The COMP5348 06 Group 1";
    }

    public static String formOrderPaymentEmailMessage(String email, long orderId, boolean paymentStatus){
        String orderPaymentStatusString = paymentStatus ? "paid successfully" : "failed to pay";
        return "Dear " + email + ",\n\n" +
                "Your order with ID " + orderId + " has been " + orderPaymentStatusString + ".\n\n" +
                "Thank you for your purchase!\n\n" +
                "Best regards,\n" +
                "The COMP5348 06 Group 1";
    }
}
