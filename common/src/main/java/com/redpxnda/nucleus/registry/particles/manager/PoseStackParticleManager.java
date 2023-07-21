package com.redpxnda.nucleus.registry.particles.manager;

import com.redpxnda.nucleus.registry.particles.Trail;
import net.minecraft.client.renderer.RenderType;

public interface PoseStackParticleManager extends DynamicParticleManager {
    void disconnect();
    void reconnect();
    /*PoseStack getPoseStack();*/
    RenderType _getRenderType();
    void setRenderType(RenderType renderType);
    Trail getTrail();
    void setTrail(Trail trail);
    void clearSavedPositions();
}
