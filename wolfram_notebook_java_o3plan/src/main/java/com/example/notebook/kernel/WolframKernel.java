package com.example.notebook.kernel;

import com.wolfram.jlink.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around Wolfram J/Link for kernel communication.
 * Provides a clean interface for evaluating Wolfram Language expressions.
 */
public class WolframKernel implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(WolframKernel.class);
    
    private final KernelLink link;
    private volatile boolean closed = false;

    public WolframKernel() throws MathLinkException {
        try {
            // Create kernel link using wolframscript
            link = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'wolframscript -wstp'");
            link.discardAnswer(); // discard initial packet
            logger.info("Wolfram kernel initialized successfully");
        } catch (MathLinkException e) {
            logger.error("Failed to initialize Wolfram kernel", e);
            throw e;
        }
    }

    /**
     * Evaluate a Wolfram Language expression and return the result as a string.
     * 
     * @param input The Wolfram Language expression to evaluate
     * @return The result formatted as OutputForm
     * @throws MathLinkException if evaluation fails
     */
    public String evaluate(String input) throws MathLinkException {
        if (closed) {
            throw new IllegalStateException("Kernel has been closed");
        }
        
        try {
            logger.debug("Evaluating: {}", input);
            String result = link.evaluateToOutputForm(input, 0);
            logger.debug("Result: {}", result);
            return result;
        } catch (MathLinkException e) {
            logger.error("Evaluation failed for input: {}", input, e);
            throw e;
        }
    }

    /**
     * Evaluate and return result as InputForm (more suitable for programmatic use).
     */
    public String evaluateToInputForm(String input) throws MathLinkException {
        if (closed) {
            throw new IllegalStateException("Kernel has been closed");
        }
        
        try {
            return link.evaluateToInputForm(input, 0);
        } catch (MathLinkException e) {
            logger.error("InputForm evaluation failed for input: {}", input, e);
            throw e;
        }
    }

    /**
     * Check if the kernel is ready for evaluation.
     */
    public boolean isReady() {
        return !closed && link != null && link.ready();
    }

    @Override
    public void close() {
        if (!closed && link != null) {
            try {
                link.close();
                logger.info("Wolfram kernel closed");
            } catch (Exception e) {
                logger.warn("Error closing Wolfram kernel", e);
            } finally {
                closed = true;
            }
        }
    }
}
