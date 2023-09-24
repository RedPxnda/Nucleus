package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.facet.doubles.ClientCapabilityListener;
import com.redpxnda.nucleus.facet.doubles.NumericalsFacet;
import com.redpxnda.nucleus.facet.doubles.RenderingMode;
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

    public static <T extends NbtElement> @Nullable Facet<T> getFacetFromSync(int entityId, Identifier facetId) {
        MinecraftClient mc = MinecraftClient.getInstance();
        World level = mc.world;
        if (level == null) return null;

        Entity entity = level.getEntityById(entityId);
        if (entity == null) return null;

        return (Facet<T>) FacetRegistry.get(facetId).get(entity);
    }
    public static <T extends NbtElement> @Nullable Facet<T> getAndSetClientEntityFacet(int entityId, Identifier capId, T data) {
        Facet<T> facet = getFacetFromSync(entityId, capId);
        if (facet != null)
            facet.loadNbt(data);
        return facet;
    }

    public static void handleClientDoublesFacetAdjustment(NumericalsFacet facet, NbtCompound data) {
        Map<String, Double> prevValues = new HashMap<>();
        facet.doubles.forEach((key, val) -> {
            RenderingMode mode = ClientCapabilityListener.renderers.get(key);
            long currentTime = Util.getMeasuringTimeMs();
            if (mode != null && mode.adjustInterpolateTarget) {
                long lastMod = facet.getModificationTime(key, -1000);
                float dif = (currentTime - lastMod) / 1000f;
                float lerpDelta = MathHelper.clamp(dif/mode.interpolateTime, 0f, 1f);
                val = mode.interpolate.interpolate(lerpDelta, facet.prevValues.getOrDefault(key, val), val);
            }

            prevValues.put(key, val);
            facet.modifications.put(key, facet.modifications.containsKey(key) ? currentTime : -1000);
        });
        facet.prevValues = prevValues;

        facet.loadNbt(data);
    }
}
