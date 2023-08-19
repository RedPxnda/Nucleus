package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.mixin.ChunkMapAccessor;
import com.redpxnda.nucleus.mixin.TrackedEntityAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface PlayerSendable {
    void send(ServerPlayer player);
    default void send(Iterable<ServerPlayer> players) {
        players.forEach(this::send);
    }
    default void send(ServerLevel level) {
        send(level.players());
    }
    default void send(MinecraftServer server) {
        send(server.getPlayerList().getPlayers());
    }

    /**
     * send to all players tracking the trackedEntity
     */
    default void sendToTrackers(Entity trackedEntity) {
        if (trackedEntity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
            ChunkMap.TrackedEntity tracked = ((ChunkMapAccessor) chunkCache.chunkMap).getEntityMap().get(trackedEntity.getId());
            if (tracked != null) ((TrackedEntityAccessor) tracked).getSeenBy().forEach(cnct -> send(cnct.getPlayer()));
        }
    }
}
