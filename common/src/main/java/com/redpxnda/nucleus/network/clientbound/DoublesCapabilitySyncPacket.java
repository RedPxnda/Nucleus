package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.capability.doubles.DoublesCapability;
import com.redpxnda.nucleus.network.ClientboundHandling;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class DoublesCapabilitySyncPacket extends CapabilitySyncPacket<CompoundTag, DoublesCapability> {
    public DoublesCapabilitySyncPacket(Entity target, DoublesCapability cap) {
        super(target, cap);
    }

    public DoublesCapabilitySyncPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        EntityCapability<CompoundTag> raw = ClientboundHandling.getEntityCap(targetId, new ResourceLocation(capId));
        if (raw instanceof DoublesCapability cap) {
            ClientboundHandling.handleClientDoublesCapabilityAdjustment(cap, capData);
        }
    }
}
