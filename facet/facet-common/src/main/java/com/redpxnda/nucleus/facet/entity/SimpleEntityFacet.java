package com.redpxnda.nucleus.facet.entity;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.facet.FacetRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * intended to be instantiated by {@link FacetRegistry#registerSimple(ResourceLocation, Codec)}
 *
 * @param <T> Custom Facet Data
 */
public class SimpleEntityFacet<T> implements CodecEntityFacet<T> {
    @Nullable
    T value;
    Codec<T> codec;
    FacetKey<SimpleEntityFacet<T>> key;
    boolean updateOnSet;
    Entity owner;
    Predicate<T> shouldSave;

    /**
     * Creates and registers a simple data holder Facet by giving it a Codec
     *
     * @param id    the id to register under. will be in entity save data
     * @param codec the codec to encode data
     * @param <T>   your custom data
     * @return the key to retrieve the data form any entity
     */
    public static <T> Builder<T> createSimple(
            ResourceLocation id,
            Codec<T> codec
    ) {
        @SuppressWarnings("unchecked")
        FacetKey<SimpleEntityFacet<T>> key =
                (FacetKey<SimpleEntityFacet<T>>)
                        (FacetKey<?>) FacetRegistry.register(id, SimpleEntityFacet.class);

        Builder<T> facetBuilder = new Builder<>(codec, key);

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            SimpleEntityFacet<T> facet = facetBuilder.getCreator().apply(entity);
            if (facet != null) {
                attacher.add(key, facet);
                if (facet.updateOnSet) {
                    facet.sendToTrackers(entity);
                }
            }
        });

        return facetBuilder;
    }

    public SimpleEntityFacet(Codec<T> codec, FacetKey<SimpleEntityFacet<T>> key, @Nullable T defaultValue, boolean updateOnSet, Entity owner, Predicate<T> shouldSave) {
        this.codec = codec;
        this.key = key;
        value = defaultValue;
        this.updateOnSet = updateOnSet;
        this.owner = owner;
    }

    @Override
    public Codec<T> getCodec() {
        return codec;
    }

    /**
     * get the value of the entity, can be null
     *
     * @return
     */
    @Nullable
    @Override
    public T get() {
        return value;
    }

    /**
     * set the value of this facet.
     * can be set to null
     *
     * @param value
     */
    @Override
    public void set(@Nullable T value) {
        this.value = value;
        sendToTrackers(owner);
    }

    @Override
    public FacetKey<SimpleEntityFacet<T>> getKey() {
        return key;
    }

    public static class Builder<T> {
        Codec<T> codec;
        FacetKey<SimpleEntityFacet<T>> key;
        T value = null;
        boolean updateOnSet = true;
        Predicate<T> shouldSave = (t->true);
        Predicate<Entity> entityPredicate = (entity -> true);

        private Builder(Codec<T> codec, FacetKey<SimpleEntityFacet<T>> key) {
            this.key = key;
            this.codec = codec;
        }

        /**
         * predicate to what entities this predicate should be attached to.
         *
         * @param entityPredicate
         * @return
         */
        public Builder<T> setPredicate(Predicate<Entity> entityPredicate) {
            this.entityPredicate = entityPredicate;
            return this;
        }

        /**
         * this can be used to skip save to files.
         * should be used to prevent saving unnecessary data
         */
        public Builder<T> setSaveCondition(Predicate<T> defaultValue) {
            this.shouldSave = defaultValue;
            return this;
        }

        /**
         * set the default value - what an unset facet should contain.
         * can be null
         */
        public Builder<T> setDefaultValue(@Nullable T defaultValue) {
            this.value = defaultValue;
            return this;
        }

        /**
         * if the Facet should be auto-synced to the clients on set
         *
         * @param shouldUpdate
         * @return
         */
        public Builder<T> syncToClientsOnSet(boolean shouldUpdate) {
            this.updateOnSet = shouldUpdate;
            return this;
        }

        private Function<Entity, SimpleEntityFacet<T>> getCreator() {
            return (entity) -> {
                if (entityPredicate.test(entity)) {
                    return new SimpleEntityFacet<>(codec, key, value, updateOnSet, entity,shouldSave);
                } else {
                    return null;
                }
            };
        }

        public FacetKey<SimpleEntityFacet<T>> build() {
            return key;
        }
    }
}
