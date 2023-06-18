package com.redpxnda.nucleus.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public interface SimplePacket {
    void toBuffer(FriendlyByteBuf buf);
    void handle(NetworkManager.PacketContext context);
    default void wrappedHandle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> handle(context));
    }
}
