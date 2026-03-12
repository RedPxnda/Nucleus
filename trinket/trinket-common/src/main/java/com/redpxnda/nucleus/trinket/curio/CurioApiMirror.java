package com.redpxnda.nucleus.trinket.curio;

import com.redpxnda.nucleus.trinket.*;
import dev.architectury.platform.Platform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CurioApiMirror implements TrinketApiMirror {
    static {
        NucleusTrinket.WATCHED_NAMESPACES.add("curios");
    }

    @Override
    public Map<CommonSlotReference, ItemStack> getItems(LivingEntity wearer) {
        Map<CommonSlotReference, ItemStack> map = new HashMap<>();
        CuriosApi.getCuriosInventory(wearer).ifPresent(curioInv -> {
            curioInv.findCurios(s -> true).forEach(slotResult -> {
                map.put(convert(slotResult.slotContext()), slotResult.stack());
            });
        });
        return map;
    }

    @Override
    public List<ItemStack> getTrinketList(LivingEntity wearer) {
        List<ItemStack> itemStacks = new ArrayList<>();
        CuriosApi.getCuriosInventory(wearer).ifPresent(curioInv -> {
            curioInv.findCurios(s -> true).forEach(slotResult -> {
                itemStacks.add(slotResult.stack());
            });
        });
        return itemStacks;
    }

    @Override
    public void registerCurioTrinket(Item item, Trinket trinket) {
        top.theillusivec4.curios.api.CuriosApi.registerCurio(item, new CuriosTrinket(trinket));
    }

    @Override
    public void registerCurioTrinketRenderer(Item item, Supplier<TrinketRenderer> renderer) {
        top.theillusivec4.curios.api.client.CuriosRendererRegistry.register(item, () -> new CurioTrinketRenderer(renderer.get()));
    }

    public boolean enabled() {
        return !Platform.isModLoaded("accessories");
    }

    @Override
    public String getTagWatchID() {
        return "curios";
    }

    public static CommonSlotReference convert(SlotContext ctx) {
        return new CommonSlotReference() {

            @Override
            public String getSlotGroupId() {
                return ctx.identifier();
            }

            @Override
            public boolean isCurious() {
                return true;
            }

            @Override
            public String getSlotId() {
                return ctx.identifier();
            }

            @Override
            public int getSlotIndex() {
                return ctx.index();
            }
        };
    }
}
