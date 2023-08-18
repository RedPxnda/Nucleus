package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.mixin.ChunkMapAccessor;
import com.redpxnda.nucleus.mixin.TrackedEntityAccessor;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public interface SimplePacket {
    void toBuffer(FriendlyByteBuf buf);
    void handle(NetworkManager.PacketContext context);
    default void wrappedHandle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> handle(context));
    }

    default void send(ServerPlayer player) {
        Nucleus.CHANNEL.sendToPlayer(player, this);
    }
    default void send(Iterable<ServerPlayer> players) {
        Nucleus.CHANNEL.sendToPlayers(players, this);
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
