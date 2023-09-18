package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.capability.entity.EntityCapability;
import com.redpxnda.nucleus.capability.entity.doubles.DoublesCapability;
import com.redpxnda.nucleus.network.ClientboundHandling;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class DoublesCapabilitySyncPacket extends CapabilitySyncPacket<NbtCompound, DoublesCapability> {
    public DoublesCapabilitySyncPacket(Entity target, DoublesCapability cap) {
        super(target, cap);
    }

    public DoublesCapabilitySyncPacket(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        EntityCapability<NbtCompound> raw = ClientboundHandling.getEntityCap(targetId, new Identifier(capId));
        if (raw instanceof DoublesCapability cap) {
            ClientboundHandling.handleClientDoublesCapabilityAdjustment(cap, capData);
        }
    }
}
