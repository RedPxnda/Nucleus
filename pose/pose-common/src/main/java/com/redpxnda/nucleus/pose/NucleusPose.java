package com.redpxnda.nucleus.pose;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.facet.network.TrackingUpdateSyncer;
import com.redpxnda.nucleus.pose.client.ClientPoseFacet;
import com.redpxnda.nucleus.pose.client.PoseAnimationResourceListener;
import com.redpxnda.nucleus.pose.network.clientbound.PoseFacetSyncPacket;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;

import static com.redpxnda.nucleus.Nucleus.loc;

public class NucleusPose {
    public static final String MOD_ID = "nucleus_pose";
    
    public static void init() {
        Nucleus.registerPacket(NetworkManager.Side.S2C, PoseFacetSyncPacket.TYPE, PoseFacetSyncPacket.STREAM_CODEC);

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            PoseAnimationResourceListener.init();
        });

        ServerPoseFacet.KEY = FacetRegistry.register(loc("entity_pose"), ServerPoseFacet.class);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () ->
                ClientPoseFacet.KEY = FacetRegistry.register(ClientPoseFacet.loc, ClientPoseFacet.class)
        );

        TrackingUpdateSyncer.register(ServerPoseFacet.KEY);

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            if (entity instanceof Player) {
                if (!entity.level().isClientSide) attacher.add(ServerPoseFacet.KEY, new ServerPoseFacet(entity));
                else attacher.add(ClientPoseFacet.KEY, new ClientPoseFacet(entity));
            }
        });
        CommandRegistrationEvent.EVENT.register((serverCommandSourceCommandDispatcher, registryAccess, listener) -> {
            PoseCommands.register(serverCommandSourceCommandDispatcher);
        });

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new PoseAnimationResourceListener())); // works for nucleus and addon namespaces
    }
}
