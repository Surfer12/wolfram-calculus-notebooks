package com.example.notebook.service;

import com.example.notebook.kernel.WolframKernel;
import com.example.notebook.model.Cell;
import com.example.notebook.model.EvaluationResult;
import com.example.notebook.persistence.CellRepository;
import com.example.notebook.persistence.ResultRepository;
import com.wolfram.jlink.MathLinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing cell evaluation history and results.
 */
@Service
public class HistoryService {
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
    
    private final CellRepository cellRepository;
    private final ResultRepository resultRepository;
    private final SessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;
    
    public HistoryService(CellRepository cellRepository, 
                         ResultRepository resultRepository,
                         SessionManager sessionManager,
                         SimpMessagingTemplate messagingTemplate) {
        this.cellRepository = cellRepository;
        this.resultRepository = resultRepository;
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Evaluate a Wolfram Language expression and store the result.
     */
    public EvaluationResult evaluate(UUID notebookId, String input) {
        logger.info("Evaluating expression for notebook {}: {}", notebookId, input);
        
        // Store the cell
        Cell cell = Cell.create(notebookId, input);
        cellRepository.insert(cell);
        
        Instant start = Instant.now();
        EvaluationResult result;
        
        try {
            WolframKernel kernel = sessionManager.getKernel(notebookId);
            String output = kernel.evaluate(input);
            Duration evalTime = Duration.between(start, Instant.now());
            
            result = EvaluationResult.success(cell.id(), output, evalTime);
            logger.info("Evaluation successful in {}ms", evalTime.toMillis());
            
        } catch (MathLinkException e) {
            Duration evalTime = Duration.between(start, Instant.now());
            result = EvaluationResult.failure(cell.id(), e.getMessage(), evalTime);
            logger.error("Evaluation failed after {}ms: {}", evalTime.toMillis(), e.getMessage());
        }
        
        // Store the result
        resultRepository.insert(result);
        
        // Broadcast result via WebSocket
        messagingTemplate.convertAndSend("/topic/notebook/" + notebookId, result);
        
        return result;
    }

    /**
     * Get evaluation history for a notebook.
     */
    public List<EvaluationResult> getHistory(UUID notebookId) {
        return resultRepository.findByNotebookId(notebookId);
    }

    /**
     * Get all cells for a notebook.
     */
    public List<Cell> getCells(UUID notebookId) {
        return cellRepository.findByNotebookId(notebookId);
    }

    /**
     * Clear all history for a notebook.
     */
    public void clearHistory(UUID notebookId) {
        logger.info("Clearing history for notebook: {}", notebookId);
        cellRepository.deleteByNotebookId(notebookId);
        // Results will be cascade deleted due to foreign key constraint
    }
}
