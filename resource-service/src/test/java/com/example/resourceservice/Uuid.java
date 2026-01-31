package com.example.resourceservice;

import java.util.regex.Pattern;

public final class Uuid {

    private static final String UUID_REGEXP = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}";

    public static boolean isValid(String s) {
        return Pattern.matches(UUID_REGEXP, s);
    }
}
