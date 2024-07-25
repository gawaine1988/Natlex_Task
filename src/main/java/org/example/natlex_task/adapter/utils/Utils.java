package org.example.natlex_task.adapter.utils;

import org.example.natlex_task.adapter.exception.ArgumentNotValidException;

import java.util.UUID;

public class Utils {
    public static void validateUUID(String sectionId) {
        try {
            UUID.fromString(sectionId);
            // Validate the format is correct
        } catch (IllegalArgumentException e) {
            throw new ArgumentNotValidException("Invalid Section Id");
        }
    }
}
