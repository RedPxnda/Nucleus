package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.Nucleus;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Supplier;

public interface SimplePacket extends PlayerSendable {
    void toBuffer(PacketByteBuf buf);
    void handle(NetworkManager.PacketContext context);
    default void wrappedHandle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> handle(context));
    }

    default void send(ServerPlayerEntity player) {
        Nucleus.CHANNEL.sendToPlayer(player, this);
    }
    default void send(Iterable<ServerPlayerEntity> players) {
        Nucleus.CHANNEL.sendToPlayers(players, this);
    }
}
