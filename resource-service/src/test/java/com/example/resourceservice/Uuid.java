package com.example.resourceservice;

import java.util.UUID;

public interface Uuid {

    static boolean isValid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
