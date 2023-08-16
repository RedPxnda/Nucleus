package com.redpxnda.nucleus.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;

public interface ServerEvents {
    PrioritizedEvent<EndDataPackReload> END_DATA_PACK_RELOAD = PrioritizedEvent.createLoop();

    interface EndDataPackReload {
        /**
         * Called after data packs on a Minecraft server have been reloaded.
         *
         * <p>If the reload was not successful, the old data packs will be kept.
         *
         * @param server the server
         * @param resourceManager the resource manager
         * @param success if the reload was successful
         */
        void endDataPackReload(MinecraftServer server, CloseableResourceManager resourceManager, boolean success);
    }
}
