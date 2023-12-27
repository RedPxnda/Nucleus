package com.redpxnda.nucleus.mixin.client;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    /*@Invoker("addParticleInternal")
    Particle addParticleInternal(ParticleOptions particleOptions, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i);*/
}
