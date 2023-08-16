package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.pose.ClientPoseCapability;
import com.redpxnda.nucleus.pose.ServerPoseCapability;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class PoseCapabilitySyncPacket extends CapabilitySyncPacket<CompoundTag, ServerPoseCapability> {
    public PoseCapabilitySyncPacket(Entity target, ServerPoseCapability cap) {
        super(target, cap);
    }

    public PoseCapabilitySyncPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.getAndSetClientEntityCap(targetId, ClientPoseCapability.loc, capData);
    }
}
