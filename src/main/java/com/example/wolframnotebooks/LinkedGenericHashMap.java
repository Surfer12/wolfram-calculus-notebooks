package com.example.wolframnotebooks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LinkedGenericHashMap<K, V> extends AbstractGenericHashMap<K, V> {
    private final LinkedHashMap<K, V> map = new LinkedHashMap<>();

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Map<K, V> asMap() {
        return map;
    }

    // Search by predicate (e.g., for Interaction metadata)
    public List<V> search(Predicate<V> predicate) {
        return map.values().stream().filter(predicate).collect(Collectors.toList());
    }
} 