package com.redpxnda.nucleus.facet.entity;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.facet.network.clientbound.FacetSyncPacket;
import com.redpxnda.nucleus.network.PlayerSendable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public interface CodecEntityFacet<T> extends EntityFacet<CompoundTag> {
    /**
     * This function exists for quick registration for Facets.
     *
     * @param id          the id to register under
     * @param constructor the constructor, return null if attackment is not desired for that entity
     * @param clazz       the facet class
     * @param <T>         Your custom facet data
     * @param <K>         your facet class
     * @return they registered key, you need this to get the facet for an entity
     */
    static <T, K extends CodecEntityFacet<T>> FacetKey<?> create(ResourceLocation id, Function<Entity, K> constructor, Class<K> clazz) {
        FacetKey<K> key = FacetRegistry.register(id, clazz);
        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            K created = constructor.apply(entity);
            if (created != null) {
                attacher.add(key, created);
            }
        });
        return key;
    }


    Codec<T> getCodec();

    T get();

    void set(T value);

    @Override
    default CompoundTag toNbt() {
        T data = get();
        if (data == null) {
            return new CompoundTag();
        }
        var codecResult = getCodec().encodeStart(NbtOps.INSTANCE, get());
        if (codecResult.isError()) {
            Nucleus.getLogger().warn(
                    "Failed to encode Facet " + getClass().getName() +
                    " " + codecResult.error().get().message()
            );
            return new CompoundTag();
        }

        Tag tag = codecResult.result().get();

        // Always wrap the codec output inside { data: <tag> }
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("data", tag);
        return wrapper;
    }


    @Override
    default void loadNbt(CompoundTag nbt) {
        Tag inner = nbt.get("data");
        if (inner == null) {
            return;
        }

        var decodeResult = getCodec().parse(NbtOps.INSTANCE, inner);

        if (decodeResult.isError()) {
            Nucleus.getLogger().warn(
                    "Failed to decode Facet " + getClass().getName() +
                    " " + decodeResult.error().get().message()
            );
            return;
        }

        set(decodeResult.result().get());
    }


    FacetKey<? extends CodecEntityFacet<T>> getKey();

    @Override
    default PlayerSendable createPacket(Entity target) {
        @SuppressWarnings("unchecked")
        FacetKey<CodecEntityFacet<T>> key = (FacetKey<CodecEntityFacet<T>>) getKey();

        return new FacetSyncPacket<>(target, key, this);
    }
}
