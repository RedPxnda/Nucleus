package com.redpxnda.nucleus.capability.entity;

import com.redpxnda.nucleus.capability.entity.EntityCapability;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.network.clientbound.CapabilitySyncPacket;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface SyncedEntityCapability<T extends Tag> extends EntityCapability<T> {
    default void sendToClients(Entity capHolder, Iterable<ServerPlayer> players) {
        createPacket(capHolder).send(players);
    }
    default void sendToTrackers(Entity capHolder) {
        createPacket(capHolder).sendToTrackers(capHolder);
    }
    default void sendToClient(ServerPlayer holder) {
        sendToClient(holder, holder);
    }
    default void sendToClient(Entity capHolder, ServerPlayer player) {
        createPacket(capHolder).send(player);
    }
    default PlayerSendable createPacket(Entity target) {
        return new CapabilitySyncPacket<>(target, this);
    }
}
