package com.redpxnda.nucleus.capability;

import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.CapabilitySyncPacket;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface SyncedEntityCapability<T extends Tag> extends EntityCapability<T> {
    default void sendToClients(Iterable<ServerPlayer> players, Entity entity) {
        createPacket(entity).send(players);
    }
    default void sendToClient(ServerPlayer player) {
        sendToClient(player, player);
    }
    default void sendToClient(Entity entity, ServerPlayer player) {
        createPacket(entity).send(player);
    }
    default SimplePacket createPacket(Entity target) {
        return new CapabilitySyncPacket<>(target, this);
    }
}
