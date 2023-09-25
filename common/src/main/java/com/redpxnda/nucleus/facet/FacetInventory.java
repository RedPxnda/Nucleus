package com.redpxnda.nucleus.facet;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class FacetInventory {
    private final Map<FacetKey<?>, Facet<?>> facets = new HashMap<>();

    public <T extends Facet<?>> @Nullable T get(FacetKey<T> key) {
        return (T) facets.get(key);
    }

    public <T extends Facet<?>> Optional<T> getOptional(FacetKey<T> key) {
        T val = (T) facets.get(key);
        return val == null ? Optional.empty() : Optional.of(val);
    }

    protected <T extends Facet<?>> T set(FacetKey<T> key, T val) {
        return (T) facets.put(key, val);
    }

    protected <T extends Facet<?>> T setIfAbsent(FacetKey<T> key, T val) {
        return (T) facets.putIfAbsent(key, val);
    }

    public <T extends Facet<?>> boolean has(FacetKey<T> key) {
        return facets.containsKey(key);
    }

    public boolean isEmpty() {
        return facets.isEmpty();
    }

    public void forEach(BiConsumer<? super FacetKey<?>, ? super Facet<?>> action) {
        facets.forEach(action);
    }

    protected Map<FacetKey<?>, Facet<?>> asMap() {
        return facets;
    }

    public static FacetInventory get(Entity entity) {
        return getInternal(entity);
    }

    public static FacetInventory get(ItemStack stack) {
        return getInternal(stack);
    }

    private static FacetInventory getInternal(Object holder) {
        return ((FacetHolder) holder).getFacets();
    }
}
