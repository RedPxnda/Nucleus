package com.redpxnda.nucleus.trinket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface TrinketApiMirror {

    void registerCurioTrinket(Item item, Trinket trinket);

    @Environment(EnvType.CLIENT)
    void registerCurioTrinketRenderer(Item item, Supplier<TrinketRenderer> renderer);

    default boolean enabled() {
        return true;
    }

    String getTagWatchID();

    default boolean fakeSlot(ResourceLocation id, AdditionalSlotComponent component) {
        if (id.getNamespace().equals(getTagWatchID())) {
            return component.slots().contains(id.getPath());
        }
        return false;
    }

    Map<CommonSlotReference, ItemStack> getItems(LivingEntity wearer);

    /**
     * has less overhead, use prefered over {@link #getItems(LivingEntity)}
     */
    List<ItemStack> getTrinketList(LivingEntity wearer);
}
