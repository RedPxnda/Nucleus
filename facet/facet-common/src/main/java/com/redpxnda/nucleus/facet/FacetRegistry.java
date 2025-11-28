package com.redpxnda.nucleus.facet;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.event.PrioritizedEvent;
import com.redpxnda.nucleus.facet.entity.SimpleEntityFacet;
import com.redpxnda.nucleus.facet.event.FacetAttachmentEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class FacetRegistry {
    private static final Logger LOGGER = Nucleus.getLogger();
    private static final Map<ResourceLocation, FacetKey<?>> REGISTERED_FACETS = new HashMap<>();
    public static final String TAG_FACETS_ID = "nucleus:facets";

    public static final PrioritizedEvent<FacetAttachmentEvent<Entity>> ENTITY_FACET_ATTACHMENT = PrioritizedEvent.createLoop();
    public static final PrioritizedEvent<FacetAttachmentEvent<ItemStack>> ITEM_FACET_ATTACHMENT = PrioritizedEvent.createLoop();
    public static final PrioritizedEvent<FacetAttachmentEvent<MobEffectInstance>> STATUS_EFFECT_FACET_ATTACHMENT = PrioritizedEvent.createLoop();

    public static <T extends Facet<?>> FacetKey<T> register(ResourceLocation id, Class<T> cls) {
        FacetKey<T> key = new FacetKey<>(id, cls);
        REGISTERED_FACETS.put(id, key);
        return key;
    }

    /**
     * Creates and registers a simple data holder Facet by giving it a Codec
     * @param id the id to register under. will be in entity save data
     * @param codec the codec to encode data
     * @param shouldAttach if it should attach
     * @return the key to retrieve the data form any entity
     * @param <T> your custom data
     */
    public static <T> FacetKey<SimpleEntityFacet<T>> registerSimple(
            ResourceLocation id,
            Codec<T> codec,
            Predicate<Entity> shouldAttach
    ) {
        return SimpleEntityFacet.createSimple(id,codec,shouldAttach);
    }

    public static FacetKey<?> get(ResourceLocation id) {
        return REGISTERED_FACETS.get(id);
    }

    public static void loadNbtToFacet(Tag element, FacetKey<?> key, Facet<?> facet) {
        if (element != null)
            try {
                ((Facet) facet).loadNbt(element);
            } catch (ClassCastException ex) {
                LOGGER.error("NBT data mismatch for Facet '" + key + "'! Tried to give '" + element + "', but it expected something different.", ex);
            }
    }

    private FacetRegistry() {}
}
