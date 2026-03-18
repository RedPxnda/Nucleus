package com.redpxnda.nucleus.util;

import com.google.common.collect.ForwardingMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PriorityMap<K> extends ForwardingMap<K, Float> {
    protected boolean hasBeenSorted = false;
    private volatile Map<K, Float> delegate;
    // Immutable snapshots (fast path)
    private volatile Set<Map.Entry<K, Float>> entrySnapshot = Set.of();
    private volatile Set<K> entrySetSnapshot = Set.of();
    private volatile List<Map.Entry<K, Float>> entryListSnapshot = List.of();

    public PriorityMap(int initialCapacity, float loadFactor) {
        delegate = Collections.synchronizedMap(new LinkedHashMap<>(initialCapacity, loadFactor));
    }

    public PriorityMap(int initialCapacity) {
        delegate = Collections.synchronizedMap(new LinkedHashMap<>(initialCapacity));
    }

    public PriorityMap() {
        delegate = Collections.synchronizedMap(new LinkedHashMap<>(0));
    }

    @Override
    protected @NotNull Map<K, Float> delegate() {
        return delegate;
    }

    public PriorityMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        delegate = Collections.synchronizedMap(new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder));
    }

    public boolean hasBeenSorted() {
        return hasBeenSorted;
    }

    /**
     * Sort the map so that the LOWEST value is first and the HIGHEST value is last
     */
    public void sort() {
        Map<K, Float> newMap;

        synchronized (delegate) {
            newMap = delegate.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));
        }

        // Build immutable snapshots (no further locking needed)
        List<Map.Entry<K, Float>> list = List.copyOf(newMap.entrySet());
        Set<Map.Entry<K, Float>> set = Set.copyOf(list);
        Set<K> kSet = newMap.keySet();

        // Publish atomically via volatile writes
        this.delegate = Collections.synchronizedMap(newMap);
        this.entryListSnapshot = list;
        this.entrySnapshot = set;
        this.entrySetSnapshot = kSet;
        this.hasBeenSorted = true;
    }

    public void sortIfUnsorted() {
        if (!hasBeenSorted()) sort();
    }

    /**
     * FAST PATH — immutable, no allocation, no locking
     */
    public List<Map.Entry<K, Float>> entries() {
        return entryListSnapshot;
    }

    @Override
    public @NotNull Set<K> keySet() {
        return entrySetSnapshot;
    }

    /**
     * FAST PATH — immutable set view
     */
    @Override
    public @NotNull Set<Map.Entry<K, Float>> entrySet() {
        return entrySnapshot;
    }

    /**
     * @return the first entry(the one with the lowest value, if sorted), or null if this is empty.
     */
    public Map.Entry<K, Float> first() {
        return entrySet().stream().findFirst().orElse(null);
    }

    /**
     * @return the last entry(the one with the highest value, if sorted), or null if this is empty.
     */
    public Map.Entry<K, Float> last() {
        if (size() == 0) return null;
        return (Map.Entry<K, Float>) entrySet().toArray()[size() - 1];
    }
}
