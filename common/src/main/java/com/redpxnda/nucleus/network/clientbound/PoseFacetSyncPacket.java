package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.pose.ClientPoseFacet;
import com.redpxnda.nucleus.pose.ServerPoseFacet;
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
        ClientboundHandling.getAndSetClientEntityFacet(targetId, ClientPoseFacet.loc, facetData);
    }
}
