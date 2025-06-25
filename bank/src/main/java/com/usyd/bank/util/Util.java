package com.usyd.bank.util;

import java.util.Random;

public class Util {
    public static String generateAccountNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomDigits = String.format("%04d", new Random().nextInt(10000));
        return  timestamp + randomDigits;
    }
}
