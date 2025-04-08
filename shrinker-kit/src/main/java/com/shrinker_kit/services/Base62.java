package com.shrinker_kit.services;

public class Base62 {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = CHARACTERS.length();

    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(CHARACTERS.charAt((int) (value % BASE)));
            value /= BASE;
        }
        while (sb.length() < 8) {
            sb.append(CHARACTERS.charAt((int) (Math.random() * BASE)));
        }
        return sb.toString();
    }
}