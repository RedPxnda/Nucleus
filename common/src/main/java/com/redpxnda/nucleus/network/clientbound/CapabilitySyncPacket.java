package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.capability.entity.SyncedEntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataRegistry;
import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CapabilitySyncPacket<T extends NbtElement, C extends SyncedEntityCapability<T>> implements SimplePacket {
    public final int targetId;
    public final String capId;
    public final T capData;

    public CapabilitySyncPacket(Entity target, C cap) {
        targetId = target.getId();
        capId = EntityDataRegistry.getIdFrom((Class) cap.getClass()).toString();
        capData = cap.toNbt();
    }

    public CapabilitySyncPacket(PacketByteBuf buf) {
        targetId = buf.readInt();
        capId = buf.readString();
        capData = (T) ByteBufUtil.readTag(buf);
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeInt(targetId);
        buf.writeString(capId);
        ByteBufUtil.writeTag(capData, buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.getAndSetClientEntityCap(targetId, new Identifier(capId), capData);
    }
}
