package com.example.notebook.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a notebook containing multiple cells.
 */
public record Notebook(
    UUID id,
    String title,
    Instant created,
    Instant updated
) {
    public static Notebook create(String title) {
        Instant now = Instant.now();
        return new Notebook(UUID.randomUUID(), title, now, now);
    }
    
    public Notebook withUpdatedTime() {
        return new Notebook(id, title, created, Instant.now());
    }
}
