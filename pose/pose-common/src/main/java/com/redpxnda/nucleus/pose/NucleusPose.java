package com.redpxnda.nucleus.pose;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.facet.network.TrackingUpdateSyncer;
import com.redpxnda.nucleus.pose.client.ClientPoseFacet;
import com.redpxnda.nucleus.pose.client.PoseAnimationResourceListener;
import com.redpxnda.nucleus.pose.network.clientbound.PoseFacetSyncPacket;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;

import static com.redpxnda.nucleus.Nucleus.loc;

public class NucleusPose {
    public static final String MOD_ID = "nucleus_pose";
    
    public static void init() {
        Nucleus.registerPacket(PoseFacetSyncPacket.class, PoseFacetSyncPacket::new);

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            PoseAnimationResourceListener.init();
        });

        ServerPoseFacet.KEY = FacetRegistry.register(loc("entity_pose"), ServerPoseFacet.class);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () ->
                ClientPoseFacet.KEY = FacetRegistry.register(ClientPoseFacet.loc, ClientPoseFacet.class)
        );

        TrackingUpdateSyncer.register(ServerPoseFacet.KEY);

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            if (entity instanceof PlayerEntity) {
                if (!entity.getWorld().isClient) attacher.add(ServerPoseFacet.KEY, new ServerPoseFacet(entity));
                else attacher.add(ClientPoseFacet.KEY, new ClientPoseFacet(entity));
            }
        });

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new PoseAnimationResourceListener())); // works for nucleus and addon namespaces
    }
}
