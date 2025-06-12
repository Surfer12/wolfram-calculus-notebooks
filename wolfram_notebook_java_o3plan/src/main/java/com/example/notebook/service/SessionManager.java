package com.example.notebook.service;

import com.example.notebook.kernel.WolframKernel;
import com.wolfram.jlink.MathLinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Wolfram kernel sessions per notebook.
 * Each notebook gets its own kernel instance to maintain state isolation.
 */
@Service
public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    
    private final Map<UUID, WolframKernel> activeKernels = new ConcurrentHashMap<>();

    /**
     * Get or create a kernel for the specified notebook.
     */
    public WolframKernel getKernel(UUID notebookId) {
        return activeKernels.computeIfAbsent(notebookId, id -> {
            try {
                logger.info("Creating new Wolfram kernel for notebook: {}", id);
                return new WolframKernel();
            } catch (MathLinkException e) {
                logger.error("Failed to create Wolfram kernel for notebook: {}", id, e);
                throw new RuntimeException("Failed to create Wolfram kernel", e);
            }
        });
    }

    /**
     * Check if a kernel exists for the notebook.
     */
    public boolean hasKernel(UUID notebookId) {
        return activeKernels.containsKey(notebookId);
    }

    /**
     * Get kernel if it exists, without creating a new one.
     */
    public Optional<WolframKernel> getExistingKernel(UUID notebookId) {
        return Optional.ofNullable(activeKernels.get(notebookId));
    }

    /**
     * Shutdown and remove the kernel for a specific notebook.
     */
    public void shutdown(UUID notebookId) {
        WolframKernel kernel = activeKernels.remove(notebookId);
        if (kernel != null) {
            try {
                kernel.close();
                logger.info("Shutdown kernel for notebook: {}", notebookId);
            } catch (Exception e) {
                logger.warn("Error shutting down kernel for notebook: {}", notebookId, e);
            }
        }
    }

    /**
     * Shutdown all active kernels.
     */
    public void shutdownAll() {
        logger.info("Shutting down all {} active kernels", activeKernels.size());
        activeKernels.forEach((id, kernel) -> {
            try {
                kernel.close();
            } catch (Exception e) {
                logger.warn("Error shutting down kernel for notebook: {}", id, e);
            }
        });
        activeKernels.clear();
    }

    /**
     * Get count of active kernels.
     */
    public int getActiveKernelCount() {
        return activeKernels.size();
    }
}
