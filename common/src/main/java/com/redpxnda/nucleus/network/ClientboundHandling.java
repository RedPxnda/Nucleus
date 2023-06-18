package com.redpxnda.nucleus.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

public class ClientboundHandling {
    public static void createClientParticle(ParticleOptions options, double x, double y, double z, double xs, double ys, double zs) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        level.addParticle(options, x, y, z, xs, ys, zs);
    }
}
