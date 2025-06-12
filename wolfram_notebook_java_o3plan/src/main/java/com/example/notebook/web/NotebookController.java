package com.example.notebook.web;

import com.example.notebook.model.EvaluationResult;
import com.example.notebook.model.Notebook;
import com.example.notebook.persistence.NotebookRepository;
import com.example.notebook.service.HistoryService;
import com.example.notebook.service.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API for notebook operations.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow CORS for development
public class NotebookController {
    private static final Logger logger = LoggerFactory.getLogger(NotebookController.class);
    
    private final NotebookRepository notebookRepository;
    private final HistoryService historyService;
    private final SessionManager sessionManager;
    
    public NotebookController(NotebookRepository notebookRepository,
                            HistoryService historyService,
                            SessionManager sessionManager) {
        this.notebookRepository = notebookRepository;
        this.historyService = historyService;
        this.sessionManager = sessionManager;
    }

    /**
     * Create a new notebook.
     */
    @PostMapping("/notebooks")
    public ResponseEntity<Notebook> createNotebook(@RequestBody Map<String, String> request) {
        String title = request.getOrDefault("title", "Untitled Notebook");
        Notebook notebook = Notebook.create(title);
        notebookRepository.insert(notebook);
        logger.info("Created notebook: {} ({})", notebook.title(), notebook.id());
        return ResponseEntity.ok(notebook);
    }

    /**
     * Get all notebooks.
     */
    @GetMapping("/notebooks")
    public ResponseEntity<List<Notebook>> getAllNotebooks() {
        List<Notebook> notebooks = notebookRepository.findAll();
        return ResponseEntity.ok(notebooks);
    }

    /**
     * Get a specific notebook.
     */
    @GetMapping("/notebooks/{id}")
    public ResponseEntity<Notebook> getNotebook(@PathVariable UUID id) {
        return notebookRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Evaluate a Wolfram Language expression in a notebook.
     */
    @PostMapping("/notebooks/{id}/cells")
    public ResponseEntity<EvaluationResult> evaluateCell(@PathVariable UUID id, 
                                                        @RequestBody Map<String, String> request) {
        String input = request.get("input");
        if (input == null || input.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Verify notebook exists
        if (notebookRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            EvaluationResult result = historyService.evaluate(id, input.trim());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error evaluating cell for notebook {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get evaluation history for a notebook.
     */
    @GetMapping("/notebooks/{id}/history")
    public ResponseEntity<List<EvaluationResult>> getHistory(@PathVariable UUID id) {
        // Verify notebook exists
        if (notebookRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<EvaluationResult> history = historyService.getHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * Clear history for a notebook.
     */
    @DeleteMapping("/notebooks/{id}/history")
    public ResponseEntity<Void> clearHistory(@PathVariable UUID id) {
        // Verify notebook exists
        if (notebookRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        historyService.clearHistory(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a notebook and its associated data.
     */
    @DeleteMapping("/notebooks/{id}")
    public ResponseEntity<Void> deleteNotebook(@PathVariable UUID id) {
        if (notebookRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Clean up kernel session
        sessionManager.shutdown(id);
        
        // Clear history (cells and results)
        historyService.clearHistory(id);
        
        // Delete notebook
        notebookRepository.deleteById(id);
        
        logger.info("Deleted notebook: {}", id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get system status.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "running",
            "activeKernels", sessionManager.getActiveKernelCount(),
            "totalNotebooks", notebookRepository.findAll().size()
        ));
    }
}
