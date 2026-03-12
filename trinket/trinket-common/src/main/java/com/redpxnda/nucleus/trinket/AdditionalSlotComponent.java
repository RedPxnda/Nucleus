package com.redpxnda.nucleus.trinket;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * Immutable component to add runtime slots to items.
 * Should work with curio and Accessories by faking the itemstack tags
 * would probably be better to hook into accessories and use their component if their loaded, but oh well, this works
 * @param slots
 */
public record AdditionalSlotComponent(List<String> slots) {
    public static final Codec<AdditionalSlotComponent> CODEC =
            Codec.STRING.listOf().xmap(
                    AdditionalSlotComponent::new,
                    AdditionalSlotComponent::slots
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, AdditionalSlotComponent> STREAM_CODEC =
            StreamCodec.of(
                    (buf, comp) -> buf.writeCollection(comp.slots, FriendlyByteBuf::writeUtf),
                    buf -> new AdditionalSlotComponent(buf.readList(FriendlyByteBuf::readUtf))
            );
    public static final DataComponentType<AdditionalSlotComponent> ADDITIONAL_SLOTS =
                    DataComponentType.<AdditionalSlotComponent>builder()
                            .persistent(AdditionalSlotComponent.CODEC)
                            .networkSynchronized(AdditionalSlotComponent.STREAM_CODEC)
                            .build();

    public static boolean hasTagForSlot(TagKey<Item> originalTag, AdditionalSlotComponent component, ItemStack stack) {
        if (NucleusTrinket.WATCHED_NAMESPACES.contains(originalTag.location().getNamespace())) {
            for (TrinketApiMirror creator : NucleusTrinket.CREATOR) {
                if (creator.fakeSlot(originalTag.location(), component)) {
                    return true;
                }
            }
        }
        return false;
    }

    public AdditionalSlotComponent(List<String> slots) {
        Objects.requireNonNull(slots, "slots");
        this.slots = List.copyOf(slots);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdditionalSlotComponent that)) return false;
        return slots.equals(that.slots);
    }

    @Override
    public String toString() {
        return "AdditionalSlotComponent{" +
               "slots=" + slots +
               '}';
    }
}