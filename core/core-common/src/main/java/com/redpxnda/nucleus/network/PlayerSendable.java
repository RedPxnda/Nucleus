package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.mixin.ThreadedAnvilChunkStorageAccessor;
import com.redpxnda.nucleus.mixin.TrackedEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

public interface PlayerSendable {
    void send(ServerPlayerEntity player);
    default void send(Iterable<ServerPlayerEntity> players) {
        players.forEach(this::send);
    }
    default void send(ServerWorld level) {
        send(level.getPlayers());
    }
    default void send(MinecraftServer server) {
        send(server.getPlayerManager().getPlayerList());
    }

    /**
     * send to all players tracking the trackedEntity
     */
    default void sendToTrackers(Entity trackedEntity) {
        if (trackedEntity.getWorld().getChunkManager() instanceof ServerChunkManager chunkCache) {
            ThreadedAnvilChunkStorage.EntityTracker tracked = ((ThreadedAnvilChunkStorageAccessor) chunkCache.threadedAnvilChunkStorage).getEntityTrackers().get(trackedEntity.getId());
            if (tracked != null) ((TrackedEntityAccessor) tracked).getListeners().forEach(cnct -> send(cnct.getPlayer()));
        }
    }
}
