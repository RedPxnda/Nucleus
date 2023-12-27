package com.redpxnda.nucleus.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

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
}
