package com.example.wolframnotebooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Comparator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MinHeapTest {
    private MinHeap<Integer> heap;
    private Comparator<Integer> comparator;

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
        heap.add(5);
        heap.add(3);
        heap.add(7);
        assertEquals(3, heap.peek());
    }

    @Test
    void testPoll() {
        heap.add(2);
        heap.add(1);
        heap.add(4);
        assertEquals(1, heap.poll());
        assertEquals(2, heap.poll());
        assertEquals(4, heap.poll());
        assertNull(heap.poll());
    }

    @Test
    void testSize() {
        assertEquals(0, heap.size());
        heap.add(10);
        assertEquals(1, heap.size());
    }

    @Test
    void testAsList() {
        heap.add(8);
        heap.add(6);
        List<Integer> list = heap.asList();
        assertTrue(list.contains(8));
        assertTrue(list.contains(6));
    }
} 