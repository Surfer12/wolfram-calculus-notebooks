package com.example.wolframnotebooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractGenericHashMapTest {
    private AbstractGenericHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = Mockito.mock(AbstractGenericHashMap.class);
    }

    @Test
    void testPutAndGet() {
        doNothing().when(map).put("a", 1);
        when(map.get("a")).thenReturn(1);
        map.put("a", 1);
        assertEquals(1, map.get("a"));
    }

    @Test
    void testContainsKey() {
        when(map.containsKey("b")).thenReturn(true);
        assertTrue(map.containsKey("b"));
    }

    @Test
    void testRemove() {
        when(map.remove("c")).thenReturn(2);
        assertEquals(2, map.remove("c"));
    }

    @Test
    void testSize() {
        when(map.size()).thenReturn(3);
        assertEquals(3, map.size());
    }

    @Test
    void testAsMap() {
        Map<String, Integer> dummy = Map.of("x", 10);
        when(map.asMap()).thenReturn(dummy);
        assertEquals(dummy, map.asMap());
    }
} 