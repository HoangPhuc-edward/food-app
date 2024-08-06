package com.example.foodapp.Domain;

import java.util.Random;

public class RandomIDGenerator {
    public static String generateRandomID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder id = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            id.append(characters.charAt(random.nextInt(characters.length())));
        }

        return id.toString();
    }
}
