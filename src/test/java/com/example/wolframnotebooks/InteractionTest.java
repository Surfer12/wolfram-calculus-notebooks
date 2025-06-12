package com.example.wolframnotebooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.Instant;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class InteractionTest {
    private Interaction interaction;
    private Map<String, String> metadata;

    @BeforeEach
    void setUp() {
        metadata = Mockito.mock(Map.class);
        interaction = new Interaction("/api/test", "req", "res", 123L, "user1", metadata);
    }

    @Test
    void testGetters() {
        assertNotNull(interaction.getId());
        assertEquals("/api/test", interaction.getEndpoint());
        assertEquals("req", interaction.getRequestPayload());
        assertEquals("res", interaction.getResponsePayload());
        assertEquals(123L, interaction.getResponseTimeMillis());
        assertEquals("user1", interaction.getUser());
        assertEquals(metadata, interaction.getMetadata());
        assertNotNull(interaction.getTimestamp());
        assertTrue(interaction.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }
} 