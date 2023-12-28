package com.redpxnda.nucleus.facet.network;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.facet.FacetRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FacetPacketHandling {
    public static <T extends NbtElement> @Nullable Facet<T> getFacetFromSync(int entityId, Identifier facetId) {
        MinecraftClient mc = MinecraftClient.getInstance();
        World level = mc.world;
        if (level == null) return null;

        Entity entity = level.getEntityById(entityId);
        if (entity == null) return null;

        return (Facet<T>) FacetRegistry.get(facetId).get(entity);
    }

    public static <T extends NbtElement> @Nullable Facet<T> getAndSetClientEntityFacet(int entityId, Identifier capId, T data) {
        Facet<T> facet = getFacetFromSync(entityId, capId);
        if (facet != null)
            facet.loadNbt(data);
        return facet;
    }
}
