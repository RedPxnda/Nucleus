package com.redpxnda.nucleus.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;

import java.util.*;

/**
 * A {@link PriorityMap} that can contain multiple of the same key.
 */
public class PriorityMultiMap<K> implements Multimap<K, Float> {
    protected final Multimap<K, Float> delegate = Multimaps.newMultimap(new LinkedHashMap<>(), ArrayList::new);
    protected boolean hasBeenSorted = false;

    public boolean hasBeenSorted() {
        return hasBeenSorted;
    }

    /**
     * Sort the map so that the LOWEST value is first and the HIGHEST value is last
     */
    public void sort() {
        Multimap<K, Float> map = entries().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Multimaps.toMultimap(Map.Entry::getKey, Map.Entry::getValue, () -> Multimaps.newMultimap(new LinkedHashMap<>(), ArrayList::new)));
        clear();
        putAll(map);
        hasBeenSorted = true;
    }
    public void sortIfUnsorted() {
        if (!hasBeenSorted()) sort();
    }

    /**
     * @return the first entry(the one with the lowest value, if sorted), or null if this is empty.
     */
    public Map.Entry<K, Float> first() {
        return delegate.entries().stream().findFirst().orElse(null);
    }

    /**
     * @return the last entry(the one with the highest value, if sorted), or null if this is empty.
     */
    public Map.Entry<K, Float> last() {
        if (size() == 0) return null;
        return (Map.Entry<K, Float>) delegate.entries().toArray()[size()-1];
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        return delegate.containsEntry(key, value);
    }

    @Override
    public boolean put(K key, Float value) {
        return delegate.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean putAll(K key, Iterable<? extends Float> values) {
        return delegate.putAll(key, values);
    }

    @Override
    public boolean putAll(Multimap<? extends K, ? extends Float> multimap) {
        return delegate.putAll(multimap);
    }

    @Override
    public Collection<Float> replaceValues(K key, Iterable<? extends Float> values) {
        return delegate.replaceValues(key, values);
    }

    @Override
    public Collection<Float> removeAll(Object key) {
        return delegate.removeAll(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Collection<Float> get(K key) {
        return delegate.get(key);
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Multiset<K> keys() {
        return delegate.keys();
    }

    @Override
    public Collection<Float> values() {
        return delegate.values();
    }

    @Override
    public Collection<Map.Entry<K, Float>> entries() {
        return delegate.entries();
    }

    @Override
    public Map<K, Collection<Float>> asMap() {
        return delegate.asMap();
    }
}
