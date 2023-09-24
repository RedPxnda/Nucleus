package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.facet.doubles.CapabilityRegistryListener;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;

import java.util.Map;

public class SyncCapabilitiesJsonPacket extends SyncJsonMapPacket {
    public SyncCapabilitiesJsonPacket(Map<String, JsonElement> elements) {
        super(elements);
    }

    public SyncCapabilitiesJsonPacket(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        CapabilityRegistryListener.fireWith(elements);
    }
}
