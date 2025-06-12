package com.example.wolframnotebooks;

import java.util.Map;

public abstract class AbstractGenericHashMap<K, V> {
    public abstract void put(K key, V value);
    public abstract V get(K key);
    public abstract boolean containsKey(K key);
    public abstract V remove(K key);
    public abstract int size();
    public abstract Map<K, V> asMap();
} 