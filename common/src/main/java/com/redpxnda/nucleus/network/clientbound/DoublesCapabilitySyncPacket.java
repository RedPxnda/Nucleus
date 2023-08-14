package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.capability.DoublesCapability;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class DoublesCapabilitySyncPacket extends CapabilitySyncPacket<CompoundTag, DoublesCapability> {
    public final Map<String, Long> modification;

    public DoublesCapabilitySyncPacket(Entity target, DoublesCapability cap, Map<String, Long> modificationTimes) {
        super(target, cap);
        modification = modificationTimes;
    }

    public DoublesCapabilitySyncPacket(FriendlyByteBuf buf) {
        super(buf);
        modification = ByteBufUtil.readLongMap(HashMap::new, buf);
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        super.toBuffer(buf);
        ByteBufUtil.writeLongMap(modification, buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        EntityCapability<CompoundTag> raw = ClientboundHandling.getEntityCap(targetId, new ResourceLocation(capId));
        if (raw instanceof DoublesCapability cap) {
            ClientboundHandling.handleClientDoublesCapabilityAdjustment(cap, capData, modification);
        }
    }
}
