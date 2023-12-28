package com.redpxnda.nucleus.facet.entity;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.network.PlayerSendable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

public interface EntityFacet<T extends NbtElement> extends Facet<T> {
    default void onRemoved(Entity entity) {}

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
        return PlayerSendable.empty();
    }
}
