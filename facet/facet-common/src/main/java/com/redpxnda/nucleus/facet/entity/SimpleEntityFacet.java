package com.redpxnda.nucleus.facet.entity;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.facet.FacetRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

/**
 * intended to be instantiated by {@link FacetRegistry#registerSimple(ResourceLocation, Codec, Predicate)}
 *
 * @param <T> Custom Facet Data
 */
public class SimpleEntityFacet<T> implements CodecEntityFacet<T> {
    T value;
    Codec<T> codec;
    FacetKey<SimpleEntityFacet<T>> key;

    /**
     * Creates and registers a simple data holder Facet by giving it a Codec
     *
     * @param id           the id to register under. will be in entity save data
     * @param codec        the codec to encode data
     * @param shouldAttach if it should attach
     * @param <T>          your custom data
     * @return the key to retrieve the data form any entity
     */
    public static <T> FacetKey<SimpleEntityFacet<T>> createSimple(
            ResourceLocation id,
            Codec<T> codec,
            Predicate<Entity> shouldAttach
    ) {
        @SuppressWarnings("unchecked")
        FacetKey<SimpleEntityFacet<T>> key =
                (FacetKey<SimpleEntityFacet<T>>)
                        (FacetKey<?>) FacetRegistry.register(id, SimpleEntityFacet.class);

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            if (shouldAttach.test(entity)) {
                attacher.add(key, new SimpleEntityFacet<>(codec, key));
            }
        });

        return key;
    }

    public SimpleEntityFacet(Codec<T> codec, FacetKey<SimpleEntityFacet<T>> key) {
        this.codec = codec;
        this.key = key;
    }

    @Override
    public Codec<T> getCodec() {
        return codec;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public FacetKey<SimpleEntityFacet<T>> getKey() {
        return key;
    }
}
