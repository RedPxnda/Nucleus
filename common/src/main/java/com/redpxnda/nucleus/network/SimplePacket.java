package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.Nucleus;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public interface SimplePacket extends PlayerSendable {
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
}
