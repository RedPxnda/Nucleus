package com.redpxnda.nucleus.facet.entity;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.network.PlayerSendable;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface EntityFacet<T extends Tag> extends Facet<T> {
    default void onRemoved(Entity entity) {
    }

    default void sendToClients(Entity capHolder, Iterable<ServerPlayer> players) {
        createPacket(capHolder).send(players);
    }

    default void sendToTrackers(Entity capHolder) {
        createPacket(capHolder).sendToTrackers(capHolder);
    }

    default void sendToClient(ServerPlayer holder) {
        if (holder.connection != null) {
            sendToClient(holder, holder);
        }
    }

    default void sendToClient(Entity capHolder, ServerPlayer player) {
        if(player.connection!=null){
            createPacket(capHolder).send(player);
        }
    }

    default PlayerSendable createPacket(Entity target) {
        return PlayerSendable.empty();
    }
}
