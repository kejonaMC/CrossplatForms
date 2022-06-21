package dev.kejona.crossplatforms;

import java.util.Map;

public class Entry<K, V> implements Map.Entry<K, V> {

    private final K key;
    private V value;

    private Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Entry<K, V> of(K key, V value) {
        return new Entry<>(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
