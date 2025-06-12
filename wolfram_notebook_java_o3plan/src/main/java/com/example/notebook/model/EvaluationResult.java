package com.example.notebook.model;

import java.time.Duration;
import java.util.UUID;

/**
 * Represents the result of evaluating a cell.
 */
public record EvaluationResult(
    UUID cellId,
    String output,
    boolean success,
    String errorMessage,
    Duration evalTime
) {
    public static EvaluationResult success(UUID cellId, String output, Duration evalTime) {
        return new EvaluationResult(cellId, output, true, null, evalTime);
    }
    
    public static EvaluationResult failure(UUID cellId, String errorMessage, Duration evalTime) {
        return new EvaluationResult(cellId, null, false, errorMessage, evalTime);
    }
}
