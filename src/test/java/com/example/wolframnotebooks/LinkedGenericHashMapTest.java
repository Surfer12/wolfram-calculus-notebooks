package com.example.wolframnotebooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.function.Predicate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LinkedGenericHashMapTest {
    private LinkedGenericHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new LinkedGenericHashMap<>();
    }

    @Test
    void testPutGetRemove() {
        map.put("a", 1);
        assertEquals(1, map.get("a"));
        assertTrue(map.containsKey("a"));
        assertEquals(1, map.remove("a"));
        assertFalse(map.containsKey("a"));
    }

    @Test
    void testSize() {
        assertEquals(0, map.size());
        map.put("b", 2);
        assertEquals(1, map.size());
    }

    @Test
    void testAsMap() {
        map.put("c", 3);
        assertEquals(3, map.asMap().get("c"));
    }

    @Test
    void testSearch() {
        map.put("x", 10);
        map.put("y", 20);
        Predicate<Integer> predicate = Mockito.mock(Predicate.class);
        when(predicate.test(10)).thenReturn(true);
        when(predicate.test(20)).thenReturn(false);
        List<Integer> result = map.search(predicate);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0));
    }
} 