package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.capability.SyncedEntityCapability;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CapabilitySyncPacket<T extends Tag, C extends SyncedEntityCapability<T>> implements SimplePacket {
    public final int targetId;
    public final String capId;
    public final T capData;

    public CapabilitySyncPacket(Entity target, C cap) {
        targetId = target.getId();
        capId = EntityDataRegistry.getIdFrom((Class) cap.getClass()).toString();
        capData = cap.toNbt();
    }

    public CapabilitySyncPacket(FriendlyByteBuf buf) {
        targetId = buf.readInt();
        capId = buf.readUtf();
        capData = (T) ByteBufUtil.readTag(buf);
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeInt(targetId);
        buf.writeUtf(capId);
        ByteBufUtil.writeTag(capData, buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.getAndSetClientEntityCap(targetId, new ResourceLocation(capId), capData);
    }
}
