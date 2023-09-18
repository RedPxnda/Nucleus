package com.redpxnda.nucleus.capability.entity;

import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.network.clientbound.CapabilitySyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

public interface SyncedEntityCapability<T extends NbtElement> extends EntityCapability<T> {
    default void sendToClients(Entity capHolder, Iterable<ServerPlayerEntity> players) {
        createPacket(capHolder).send(players);
    }
    default void sendToTrackers(Entity capHolder) {
        createPacket(capHolder).sendToTrackers(capHolder);
    }
    default void sendToClient(ServerPlayerEntity holder) {
        sendToClient(holder, holder);
    }
    default void sendToClient(Entity capHolder, ServerPlayerEntity player) {
        createPacket(capHolder).send(player);
    }
    default PlayerSendable createPacket(Entity target) {
        return new CapabilitySyncPacket<>(target, this);
    }
}
