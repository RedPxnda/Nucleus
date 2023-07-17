package com.redpxnda.nucleus.registry.particles.manager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;

public interface PoseStackParticleManager extends DynamicParticleManager {
    void disconnect();
    void reconnect();
    PoseStack getPoseStack();
    RenderType _getRenderType();
    void setRenderType(RenderType renderType);
}
