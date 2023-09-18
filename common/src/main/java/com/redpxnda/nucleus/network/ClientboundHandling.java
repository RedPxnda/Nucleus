package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.capability.entity.EntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import com.redpxnda.nucleus.capability.entity.EntityDataRegistry;
import com.redpxnda.nucleus.capability.entity.doubles.ClientCapabilityListener;
import com.redpxnda.nucleus.capability.entity.doubles.DoublesCapability;
import com.redpxnda.nucleus.capability.entity.doubles.RenderingMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientboundHandling {
    public static void createClientParticle(ParticleEffect options, double x, double y, double z, double xs, double ys, double zs) {
        MinecraftClient mc = MinecraftClient.getInstance();
        World level = mc.world;
        if (level == null) return;

        level.addParticle(options, x, y, z, xs, ys, zs);
    }

    public static void playClientSound(double x, double y, double z, SoundEvent event, SoundCategory category, float volume, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        World level = mc.world;
        if (level == null) return;

        level.playSound(
                x, y, z,
                event, category,
                volume, pitch,
                false
        );
    }

    public static <T extends NbtElement> @Nullable EntityCapability<T> getEntityCap(int entityId, Identifier capId) {
        MinecraftClient mc = MinecraftClient.getInstance();
        World level = mc.world;
        if (level == null) return null;

        Entity entity = level.getEntityById(entityId);
        if (entity == null) return null;

        return (EntityCapability<T>) EntityDataManager.getOrCreateCapability(entity, EntityDataRegistry.getFromId(capId));
    }
    public static <T extends NbtElement> @Nullable EntityCapability<T> getAndSetClientEntityCap(int entityId, Identifier capId, T data) {
        EntityCapability<T> cap = getEntityCap(entityId, capId);
        if (cap != null)
            cap.loadNbt(data);
        return cap;
    }

    public static void handleClientDoublesCapabilityAdjustment(DoublesCapability cap, NbtCompound capData) {
        Map<String, Double> prevValues = new HashMap<>();
        cap.doubles.forEach((key, val) -> {
            RenderingMode mode = ClientCapabilityListener.renderers.get(key);
            long currentTime = Util.getMeasuringTimeMs();
            if (mode != null && mode.adjustInterpolateTarget) {
                long lastMod = cap.getModificationTime(key, -1000);
                float dif = (currentTime - lastMod) / 1000f;
                float lerpDelta = MathHelper.clamp(dif/mode.interpolateTime, 0f, 1f);
                val = mode.interpolate.interpolate(lerpDelta, cap.prevValues.getOrDefault(key, val), val);
            }

            prevValues.put(key, val);
            cap.modifications.put(key, cap.modifications.containsKey(key) ? currentTime : -1000);
        });
        cap.prevValues = prevValues;

        cap.loadNbt(capData);
    }
}
