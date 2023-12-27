package com.redpxnda.nucleus;

import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.facet.TrackingUpdateSyncer;
import com.redpxnda.nucleus.network.clientbound.PoseFacetSyncPacket;
import com.redpxnda.nucleus.pose.ClientPoseFacet;
import com.redpxnda.nucleus.pose.PoseAnimationResourceListener;
import com.redpxnda.nucleus.pose.ServerPoseFacet;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;

import static com.redpxnda.nucleus.Nucleus.loc;

public class NucleusPose {
    public static final String MOD_ID = "nucleus-pose";
    
    public static void init() {
        TrackingUpdateSyncer.register(ServerPoseFacet.KEY);
        Nucleus.registerPacket(PoseFacetSyncPacket.class, PoseFacetSyncPacket::new);

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            PoseAnimationResourceListener.init();
        });

        ServerPoseFacet.KEY = FacetRegistry.register(loc("entity_pose"), ServerPoseFacet.class);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () ->
                ClientPoseFacet.KEY = FacetRegistry.register(ClientPoseFacet.loc, ClientPoseFacet.class)
        );

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            if (entity instanceof PlayerEntity) {
                if (!entity.getWorld().isClient) attacher.add(ServerPoseFacet.KEY, new ServerPoseFacet(entity));
                else attacher.add(ClientPoseFacet.KEY, new ClientPoseFacet(entity));
            }
        });

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new PoseAnimationResourceListener())); // works for nucleus and addon namespaces
    }
}
