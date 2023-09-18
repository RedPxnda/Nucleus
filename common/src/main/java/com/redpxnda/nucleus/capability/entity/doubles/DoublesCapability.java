package com.redpxnda.nucleus.capability.entity.doubles;

import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import com.redpxnda.nucleus.capability.entity.SyncedEntityCapability;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.network.clientbound.DoublesCapabilitySyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DoublesCapability implements SyncedEntityCapability<NbtCompound> {
    public static final Map<String, Double> defaultValues = new HashMap<>();

    public final Map<String, Double> doubles = new HashMap<>();
    public Map<String, Long> modifications = new HashMap<>(); // only used and modified by client
    public Map<String, Double> prevValues = new HashMap<>(); // only used and modified by client

    public static DoublesCapability getAllFor(Entity entity) {
        return EntityDataManager.getCapability(entity, DoublesCapability.class);
    }

    public DoublesCapability(Entity entity) {
        doubles.putAll(defaultValues);
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        doubles.forEach(tag::putDouble);
        return tag;
    }

    @Override
    public void loadNbt(NbtCompound tag) {
        doubles.clear();
        tag.getKeys().forEach(key -> {
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
        modifications.put(loc, Util.getMeasuringTimeMs());
    }
    public @Nullable Long getModificationTime(String loc) {
        return modifications.get(loc);
    }
    public long getModificationTime(String loc, long ifFailed) {
        return modifications.getOrDefault(loc, ifFailed);
    }

    @Override
    public PlayerSendable createPacket(Entity target) {
        return new DoublesCapabilitySyncPacket(target, this);
    }
}
