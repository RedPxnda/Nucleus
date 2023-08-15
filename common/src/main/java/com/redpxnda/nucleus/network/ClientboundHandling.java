package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.capability.doubles.DoublesCapability;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.capability.doubles.ClientCapabilityListener;
import com.redpxnda.nucleus.capability.doubles.RenderingMode;
import com.redpxnda.nucleus.impl.EntityDataManager;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientboundHandling {
    public static void createClientParticle(ParticleOptions options, double x, double y, double z, double xs, double ys, double zs) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        level.addParticle(options, x, y, z, xs, ys, zs);
    }

    public static void playClientSound(double x, double y, double z, SoundEvent event, SoundSource category, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        level.playLocalSound(
                x, y, z,
                event, category,
                volume, pitch,
                false
        );
    }

    public static <T extends Tag> @Nullable EntityCapability<T> getEntityCap(int entityId, ResourceLocation capId) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return null;

        Entity entity = level.getEntity(entityId);
        if (entity == null) return null;

        return (EntityCapability<T>) EntityDataManager.getOrCreateCapability(entity, EntityDataRegistry.getFromId(capId));
    }
    public static <T extends Tag> @Nullable EntityCapability<T> getAndSetClientEntityCap(int entityId, ResourceLocation capId, T data) {
        EntityCapability<T> cap = getEntityCap(entityId, capId);
        if (cap != null)
            cap.loadNbt(data);
        return cap;
    }

    public static void handleClientDoublesCapabilityAdjustment(DoublesCapability cap, CompoundTag capData) {
        Map<String, Double> prevValues = new HashMap<>();
        cap.doubles.forEach((key, val) -> {
            RenderingMode mode = ClientCapabilityListener.renderers.get(key);
            long currentTime = Util.getMillis();
            if (mode != null && mode.adjustInterpolateTarget) {
                long lastMod = cap.getModificationTime(key, -1000);
                float dif = (currentTime - lastMod) / 1000f;
                float lerpDelta = Mth.clamp(dif/mode.interpolateTime, 0f, 1f);
                val = mode.interpolate.interpolate(lerpDelta, cap.prevValues.getOrDefault(key, val), val);
            }

            prevValues.put(key, val);
            cap.modifications.put(key, cap.modifications.containsKey(key) ? currentTime : -1000);
        });
        cap.prevValues = prevValues;

        cap.loadNbt(capData);
    }
}
