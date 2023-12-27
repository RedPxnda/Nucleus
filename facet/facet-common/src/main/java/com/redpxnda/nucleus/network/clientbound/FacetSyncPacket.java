package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.facet.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.network.FacetPacketHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FacetSyncPacket<T extends NbtElement, C extends EntityFacet<T>> implements SimplePacket {
    public final int targetId;
    public final String facetId;
    public final T facetData;

    public FacetSyncPacket(Entity target, FacetKey<C> key, C cap) {
        targetId = target.getId();
        facetId = key.id().toString();
        facetData = cap.toNbt();
    }

    public FacetSyncPacket(PacketByteBuf buf) {
        targetId = buf.readInt();
        facetId = buf.readString();
        facetData = (T) ByteBufUtil.readTag(buf);
    }

    @Override
    public void toBuffer(PacketByteBuf buf) {
        buf.writeInt(targetId);
        buf.writeString(facetId);
        ByteBufUtil.writeTag(facetData, buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        FacetPacketHandling.getAndSetClientEntityFacet(targetId, new Identifier(facetId), facetData);
    }
}
