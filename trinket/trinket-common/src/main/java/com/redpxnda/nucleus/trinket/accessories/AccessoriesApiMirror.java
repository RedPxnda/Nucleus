package com.redpxnda.nucleus.trinket.accessories;

import com.redpxnda.nucleus.trinket.*;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.data.EntitySlotLoader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AccessoriesApiMirror implements TrinketApiMirror {
    static {
        NucleusTrinket.WATCHED_NAMESPACES.add("accessories");
    }

    @Override
    public void registerCurioTrinket(Item item, Trinket trinket) {
        io.wispforest.accessories.api.AccessoriesAPI.registerAccessory(item, new AccessoriesTrinket(trinket));
    }

    @Override
    public void registerCurioTrinketRenderer(Item item, Supplier<TrinketRenderer> renderer) {
        io.wispforest.accessories.api.client.AccessoriesRendererRegistry.registerRenderer(item, () -> new AccessoriesTrinketRenderer(renderer.get()));
    }

    @Override
    public String getTagWatchID() {
        return "accessories";
    }

    @Override
    public Map<CommonSlotReference, ItemStack> getItems(LivingEntity wearer) {
        Map<CommonSlotReference, ItemStack> map = new HashMap<>();
        EntitySlotLoader.getEntitySlots(wearer).forEach((id, slot) -> {
            SlotReference slotReference = SlotReference.of(wearer, slot.name(), slot.amount());
            ItemStack stack = slotReference.getStack();
            if (stack != null && !stack.isEmpty()) {
                map.put(convert(slotReference), slotReference.getStack());
            }
        });
        return map;
    }

    @Override
    public List<ItemStack> getTrinketList(LivingEntity wearer) {
        List<ItemStack> list = new ArrayList<>();
        EntitySlotLoader.getEntitySlots(wearer).forEach((id, slot) -> {
            SlotReference slotReference = SlotReference.of(wearer, slot.name(), slot.amount());
            ItemStack stack = slotReference.getStack();
            if (stack != null && !stack.isEmpty()) {
                list.add(stack);
            }
        });
        return list;
    }


    public static CommonSlotReference convert(SlotReference reference) {
        return new CommonSlotReference() {
            @Override
            public String getSlotGroupId() {
                return reference.createSlotPath();
            }

            @Override
            public boolean isCurious() {
                return false;
            }

            @Override
            public String getSlotId() {
                return reference.slotName();
            }

            @Override
            public int getSlotIndex() {
                return reference.slot();
            }
        };
    }
}
