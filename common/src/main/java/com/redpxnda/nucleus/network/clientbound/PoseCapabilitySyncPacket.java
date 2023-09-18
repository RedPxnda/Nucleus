package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.pose.ClientPoseCapability;
import com.redpxnda.nucleus.pose.ServerPoseCapability;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class PoseCapabilitySyncPacket extends CapabilitySyncPacket<NbtCompound, ServerPoseCapability> {
    public PoseCapabilitySyncPacket(Entity target, ServerPoseCapability cap) {
        super(target, cap);
    }

    public PoseCapabilitySyncPacket(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.getAndSetClientEntityCap(targetId, ClientPoseCapability.loc, capData);
    }
}
