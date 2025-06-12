package com.example.wolframnotebooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MinHeapTest {
    private MinHeap<Integer> heap;
    private Comparator<Integer> comparator;
    private static final ResourceBundle messages = ResourceBundle.getBundle("TestMessages");

    @BeforeEach
    void setUp() {
        comparator = Mockito.mock(Comparator.class);
        when(comparator.compare(anyInt(), anyInt())).thenAnswer(invocation -> {
            Integer a = invocation.getArgument(0);
            Integer b = invocation.getArgument(1);
            return Integer.compare(a, b);
        });
        heap = new MinHeap<>(comparator);
    }

    @Test
    void testAddAndPeek() {
        heap.add(Integer.parseInt(messages.getString("test.value.five")));
        heap.add(Integer.parseInt(messages.getString("test.value.three")));
        heap.add(Integer.parseInt(messages.getString("test.value.seven")));
        assertEquals(Integer.parseInt(messages.getString("test.value.three")), heap.peek());
    }

    @Test
    void testPoll() {
        heap.add(Integer.parseInt(messages.getString("test.value.two")));
        heap.add(Integer.parseInt(messages.getString("test.value.one")));
        heap.add(Integer.parseInt(messages.getString("test.value.four")));
        assertEquals(Integer.parseInt(messages.getString("test.value.one")), heap.poll());
        assertEquals(Integer.parseInt(messages.getString("test.value.two")), heap.poll());
        assertEquals(Integer.parseInt(messages.getString("test.value.four")), heap.poll());
        assertNull(heap.poll());
    }

    @Test
    void testSize() {
        assertEquals(0, heap.size());
        heap.add(Integer.parseInt(messages.getString("test.value.ten")));
        assertEquals(1, heap.size());
    }

    @Test
    void testAsList() {
        heap.add(Integer.parseInt(messages.getString("test.value.eight")));
        heap.add(Integer.parseInt(messages.getString("test.value.six")));
        List<Integer> list = heap.asList();
        assertTrue(list.contains(Integer.parseInt(messages.getString("test.value.eight"))));
        assertTrue(list.contains(Integer.parseInt(messages.getString("test.value.six"))));
    }
} 