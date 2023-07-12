package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.network.clientbound.PlaySoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

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
}
