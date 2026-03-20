package com.redpxnda.nucleus.util;

import com.google.common.collect.ForwardingMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * boy this is a mess
 * This class is required to be
 * - thread save read/write
 * - always be sorted when iterated
 * - not sort on entry
 * - as fast as possible iteration and read
 * this is achieved by internally using a {@link Collections.SynchronizedSortedMap} to ensure its threadsafety.
 * but also relies on {@link LinkedHashMap} to ensure order
 * to ensure iteration speed even in high demand situations
 * we create iteration snapshots after every sort similar in function to a {@link java.util.concurrent.ConcurrentHashMap}
 * Yep. this mixes like every threaded design choice in one.
 * No, im not happy about it
 * No, i couldn't figure out a better way to accomplish this other than effectively re-implementing a {@link java.util.concurrent.ConcurrentHashMap} with as an {@link SequencedMap}
 * and that would have been far more work and far more potential bugs.
 *
 *
 * propably could be moved to a {@link java.util.concurrent.ConcurrentHashMap} and simply keep the ordered iterator calls and sort when they are called.
 * but since the last 3 probably this will work didnt in this class im not touching it since the performance difference is meaningless, the iteration over sorted entries
 * would remain the same, and thats the only real performance that matters.
 * @param <K>
 */
public class PriorityMap<K> extends ForwardingMap<K, Float> {
    protected boolean hasBeenSorted = false;
    private volatile Map<K, Float> delegate;
    // Immutable snapshots (fast path)
    private volatile Set<Map.Entry<K, Float>> entrySnapshot = Set.of();
    private volatile Set<K> entrySetSnapshot = Set.of();
    private volatile List<Map.Entry<K, Float>> entryListSnapshot = List.of();
    private final Object lock = new Object();

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

        synchronized (lock) {
            newMap = Collections.synchronizedMap(delegate.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    )));
            // Build immutable snapshots (no further locking needed)
            List<Map.Entry<K, Float>> list = List.copyOf(newMap.entrySet());
            Set<Map.Entry<K, Float>> set = LinkedHashSet.newLinkedHashSet(list.size());
            set.addAll(list);
            Set<K> kSet = LinkedHashSet.newLinkedHashSet(list.size());
            kSet.addAll(newMap.keySet());

            // Publish atomically via volatile writes
            this.entryListSnapshot = list;
            this.entrySnapshot = set;
            this.entrySetSnapshot = kSet;
            this.hasBeenSorted = true;
            this.delegate = newMap;
        }
    }

    @Override
    public Float put(K key,  Float value) {
        synchronized (lock){
            hasBeenSorted = false;
            return delegate().put(key, value);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
        synchronized (lock){
            hasBeenSorted = false;
            delegate().putAll(map);
        }
    }

    public void sortIfUnsorted() {
        if (!hasBeenSorted()) sort();
    }

    /**
     * FAST PATH — immutable, no allocation, no locking
     */
    public List<Map.Entry<K, Float>> entries() {
        sortIfUnsorted();
        return entryListSnapshot;
    }

    @Override
    public @NotNull Set<K> keySet() {
        sortIfUnsorted();
        return entrySetSnapshot;
    }

    /**
     * FAST PATH — immutable set view
     */
    @Override
    public @NotNull Set<Map.Entry<K, Float>> entrySet() {
        sortIfUnsorted();
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
