package com.redpxnda.nucleus.registry.particles.manager;

import com.redpxnda.nucleus.registry.particles.Trail;
import net.minecraft.client.render.RenderLayer;

public interface PoseStackParticleManager extends DynamicParticleManager {
    void disconnect();
    void reconnect();
    /*PoseStack getPoseStack();*/
    RenderLayer _getRenderType();
    void setRenderType(RenderLayer renderType);
    Trail getTrail();
    void setTrail(Trail trail);
    void clearSavedPositions();
}
