package com.redpxnda.nucleus.capability;

import com.redpxnda.nucleus.impl.EntityDataManager;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.DoublesCapabilitySyncPacket;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DoublesCapability implements SyncedEntityCapability<CompoundTag> {
    public static final Map<String, Double> defaultValues = new HashMap<>();

    public final Map<String, Double> doubles = new HashMap<>();
    public Map<String, Long> modifications = new HashMap<>();
    public Map<String, Double> prevValues = new HashMap<>(); // only used by client

    public static DoublesCapability getAllFor(Entity entity) {
        return EntityDataManager.getCapability(entity, DoublesCapability.class);
    }

    public DoublesCapability() {
        doubles.putAll(defaultValues);
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        doubles.forEach(tag::putDouble);
        return tag;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        doubles.clear();
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
            update(loc);
        }
        return value;
    }
    public void set(String loc, double value) {
        doubles.put(loc, value);
        update(loc);
    }
    public void update(String loc) {
        modifications.put(loc, Util.getMillis());
    }
    public @Nullable Long getModificationTime(String loc) {
        return modifications.get(loc);
    }
    public long getModificationTime(String loc, long ifFailed) {
        return modifications.getOrDefault(loc, ifFailed);
    }

    @Override
    public SimplePacket createPacket(Entity target) {
        return new DoublesCapabilitySyncPacket(target, this, modifications);
    }
}
