package com.redpxnda.nucleus.capability;

import com.redpxnda.nucleus.datapack.json.listeners.CapabilityRegistryListener;
import com.redpxnda.nucleus.impl.EntityDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DoublesCapability implements EntityCapability<CompoundTag> {
    public static final Map<String, Double> defaultValues = new HashMap<>();
    public static final Map<String, CapabilityRegistryListener.RenderingMode> renderers = new HashMap<>();

    public final Map<String, Double> doubles = new HashMap<>();

    public static DoublesCapability getAllFor(Entity entity) {
        return EntityDataManager.getCapability(entity, DoublesCapability.class);
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        doubles.forEach(tag::putDouble);
        return tag;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> {
            doubles.put(key, tag.getDouble(key));
        });
    }

    public @Nullable Double get(String loc) {
        return doubles.getOrDefault(loc, defaultValues.get(loc));
    }
    public double get(String loc, double ifFailed) {
        return doubles.getOrDefault(loc, ifFailed);
    }
    public double getOrAdd(String loc, double ifFailed) {
        Double value = doubles.getOrDefault(loc, ifFailed);
        if (value == null) {
            value = ifFailed;
            doubles.put(loc, value);
        }
        return value;
    }
    public void set(String loc, double value) {
        doubles.put(loc, value);
    }
}
