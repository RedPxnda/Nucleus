package com.redpxnda.nucleus.facet;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.event.PrioritizedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class FacetRegistry {
    private static final Map<Identifier, FacetKey<?>> REGISTERED_FACETS = new HashMap<>();
    public static final String TAG_FACETS_ID = "nucleus:facets";

    public static final PrioritizedEvent<FacetAttachmentEvent<Entity>> ENTITY_FACET_ATTACHMENT = PrioritizedEvent.createLoop();
    public static final PrioritizedEvent<FacetAttachmentEvent<ItemStack>> ITEM_FACET_ATTACHMENT = PrioritizedEvent.createLoop();

    public static <T extends Facet<?>> FacetKey<T> register(Identifier id, Class<T> cls) {
        FacetKey<T> key = new FacetKey<>(id, cls);
        REGISTERED_FACETS.put(id, key);
        return key;
    }

    public static FacetKey<?> get(Identifier id) {
        return REGISTERED_FACETS.get(id);
    }

    public static void loadNbtToFacet(NbtElement element, FacetKey<?> key, Facet<?> facet) {
        if (element != null)
            try {
                ((Facet) facet).loadNbt(element);
            } catch (ClassCastException ex) {
                Nucleus.LOGGER.error("NBT data mismatch for Facet '{}'! Tried to give '{}', but it expected something different.", key, element);
                throw new RuntimeException("Facet received unexpected NBT type during NBT load. Check logger error above.", ex);
            }
    }

    private FacetRegistry() {}
}
