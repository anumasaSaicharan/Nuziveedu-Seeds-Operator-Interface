package com.nsl.operatorInterface.utility;

import java.security.SecureRandom;

public class UIDGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateUID(String plantCode) {
        // Ensure plantCode is exactly 4 chars (pad with zeros if short)
        plantCode = plantCode == null ? "" : plantCode.trim().toUpperCase();
        if (plantCode.length() < 4) {
            plantCode = String.format("%4s", plantCode).replace(' ', '0');
        } else if (plantCode.length() > 4) {
            plantCode = plantCode.substring(0, 4);
        }

        // Generate 8 random alphanumeric characters
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            randomPart.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }

        return plantCode + randomPart;
    }
}
