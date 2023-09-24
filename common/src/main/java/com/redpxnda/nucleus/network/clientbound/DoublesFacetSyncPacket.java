package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.facet.doubles.NumericalsFacet;
import com.redpxnda.nucleus.network.ClientboundHandling;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class DoublesFacetSyncPacket extends FacetSyncPacket<NbtCompound, NumericalsFacet> {
    public DoublesFacetSyncPacket(Entity target, NumericalsFacet cap) {
        super(target, NumericalsFacet.KEY, cap);
    }

    public DoublesFacetSyncPacket(PacketByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Facet<NbtCompound> raw = ClientboundHandling.getFacetFromSync(targetId, new Identifier(facetId));
        if (raw instanceof NumericalsFacet cap) {
            ClientboundHandling.handleClientDoublesFacetAdjustment(cap, facetData);
        }
    }
}
