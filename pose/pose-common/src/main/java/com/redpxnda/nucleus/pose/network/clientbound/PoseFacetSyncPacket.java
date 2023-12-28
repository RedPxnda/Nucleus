package com.redpxnda.nucleus.pose.network.clientbound;

import com.redpxnda.nucleus.facet.network.FacetPacketHandling;
import com.redpxnda.nucleus.facet.network.clientbound.FacetSyncPacket;
import com.redpxnda.nucleus.pose.client.ClientPoseFacet;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class PoseFacetSyncPacket extends FacetSyncPacket<NbtCompound, ServerPoseFacet> {
    public PoseFacetSyncPacket(Entity target, ServerPoseFacet cap) {
        super(target, ServerPoseFacet.KEY, cap);
    }

    public PoseFacetSyncPacket(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        FacetPacketHandling.getAndSetClientEntityFacet(targetId, ClientPoseFacet.loc, facetData);
    }
}
