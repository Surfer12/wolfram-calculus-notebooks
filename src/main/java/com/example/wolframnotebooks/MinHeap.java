package com.example.wolframnotebooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MinHeap<T> {
    private final List<T> heap;
    private final Comparator<T> comparator;

    public MinHeap(Comparator<T> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    public void add(T value) {
        heap.add(value);
        siftUp(heap.size() - 1);
    }

    public T peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    public T poll() {
        if (heap.isEmpty()) return null;
        T result = heap.get(0);
        T last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return result;
    }

    public int size() {
        return heap.size();
    }

    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (comparator.compare(heap.get(idx), heap.get(parent)) >= 0) break;
            swap(idx, parent);
            idx = parent;
        }
    }

    private void siftDown(int idx) {
        int size = heap.size();
        while (true) {
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;
            int smallest = idx;
            if (left < size && comparator.compare(heap.get(left), heap.get(smallest)) < 0) smallest = left;
            if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) < 0) smallest = right;
            if (smallest == idx) break;
            swap(idx, smallest);
            idx = smallest;
        }
    }

    private void swap(int i, int j) {
        T tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }

    public List<T> asList() {
        return new ArrayList<>(heap);
    }
} 