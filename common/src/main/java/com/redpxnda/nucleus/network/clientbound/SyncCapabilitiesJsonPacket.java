package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.capability.doubles.CapabilityRegistryListener;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;

public class SyncCapabilitiesJsonPacket extends SyncJsonMapPacket {
    public SyncCapabilitiesJsonPacket(Map<String, JsonElement> elements) {
        super(elements);
    }

    public SyncCapabilitiesJsonPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        CapabilityRegistryListener.fireWith(elements);
    }
}
