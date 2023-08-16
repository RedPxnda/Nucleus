package com.redpxnda.nucleus.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PriorityMap<K> extends LinkedHashMap<K, Float> {
    protected boolean hasBeenSorted = false;

    public PriorityMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public PriorityMap(int initialCapacity) {
        super(initialCapacity);
    }

    public PriorityMap() {
        super();
    }

    public PriorityMap(Map<? extends K, ? extends Float> m) {
        super(m);
    }

    public PriorityMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public boolean hasBeenSorted() {
        return hasBeenSorted;
    }

    public void sort() {
        LinkedHashMap<K, Float> map = entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        clear();
        putAll(map);
        hasBeenSorted = true;
    }
    public void sortIfUnsorted() {
        if (!hasBeenSorted()) sort();
    }

    /**
     * @return the first entry(the one with the highest value, if sorted), or null if this is empty.
     */
    public Map.Entry<K, Float> first() {
        return entrySet().stream().findFirst().orElse(null);
    }

    /**
     * @return the last entry(the one with the lowest value, if sorted), or null if this is empty.
     */
    public Map.Entry<K, Float> last() {
        if (size() == 0) return null;
        return (Map.Entry<K, Float>) entrySet().toArray()[size()-1];
    }
}
