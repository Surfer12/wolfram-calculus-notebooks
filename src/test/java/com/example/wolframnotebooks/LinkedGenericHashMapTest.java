package com.example.wolframnotebooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.function.Predicate;
import java.util.List;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LinkedGenericHashMapTest {
    private LinkedGenericHashMap<String, Integer> map;
    private static final ResourceBundle messages = ResourceBundle.getBundle("TestMessages");

    @BeforeEach
    void setUp() {
        map = new LinkedGenericHashMap<>();
    }

    @Test
    void testPutGetRemove() {
        String key = messages.getString("test.key.a");
        map.put(key, 1);
        assertEquals(1, map.get(key));
        assertTrue(map.containsKey(key));
        assertEquals(1, map.remove(key));
        assertFalse(map.containsKey(key));
    }

    @Test
    void testSize() {
        assertEquals(0, map.size());
        String key = messages.getString("test.key.b");
        map.put(key, 2);
        assertEquals(1, map.size());
    }

    @Test
    void testAsMap() {
        String key = messages.getString("test.key.c");
        map.put(key, 3);
        assertEquals(3, map.asMap().get(key));
    }

    @Test
    void testSearch() {
        String keyX = messages.getString("test.key.x");
        String keyY = messages.getString("test.key.y");
        map.put(keyX, 10);
        map.put(keyY, 20);
        Predicate<Integer> predicate = Mockito.mock(Predicate.class);
        when(predicate.test(10)).thenReturn(true);
        when(predicate.test(20)).thenReturn(false);
        List<Integer> result = map.search(predicate);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0));
    }
} 