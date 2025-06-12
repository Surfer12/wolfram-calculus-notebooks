package com.example.notebook.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single cell in a notebook containing Wolfram Language code.
 */
public record Cell(
    UUID id,
    UUID notebookId,
    String input,
    Instant submittedAt
) {
    public static Cell create(UUID notebookId, String input) {
        return new Cell(UUID.randomUUID(), notebookId, input, Instant.now());
    }
}
