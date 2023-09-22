package com.redpxnda.nucleus.capability.item;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class ItemStackDataManager {
    private static ItemStackDataSaver getAsDataSaver(ItemStack item) {
        return ((ItemStackDataSaver) (Object) item);
    }

    public static <T extends ItemStackCapability<?>> @Nullable T getCapability(ItemStack item, Class<T> cap) {
        if (item.isEmpty()) return null;
        String id = ItemStackDataRegistry.CAPABILITIES.get(cap).id().toString();
        return (T) getAsDataSaver(item).getCapabilities().get(id);
    }

    public static <T extends ItemStackCapability<?>> T getCapability(ItemStack item, Class<T> cap, T ifFailed) {
        if (item.isEmpty()) return ifFailed;
        String id = ItemStackDataRegistry.CAPABILITIES.get(cap).id().toString();
        return (T) getAsDataSaver(item).getCapabilities().getOrDefault(id, ifFailed);
    }

    public static <T extends ItemStackCapability<?>> T getOrCreateCapability(ItemStack item, Class<T> cap) {
        T inst = getCapability(item, cap);
        if (inst == null) {
            inst = (T) ItemStackDataRegistry.CAPABILITIES.get(cap).construct(item);
            getAsDataSaver(item).getCapabilities().put(ItemStackDataRegistry.getIdFrom(cap).toString(), inst);
        }
        return inst;
    }

    public static <T extends ItemStackCapability<?>> T getOrCreateCapability(ItemStack item, Class<T> cap, T ifFailed) {
        if (item.isEmpty()) throw new IllegalArgumentException("You cannot set capabilities for empty ItemStacks!");
        T inst = getCapability(item, cap);
        if (inst == null) {
            inst = ifFailed;
            getAsDataSaver(item).getCapabilities().put(ItemStackDataRegistry.getIdFrom(cap).toString(), inst);
        }
        return inst;
    }


    public static <T extends ItemStackCapability<?>> boolean canHaveCapability(ItemStack item, Class<T> cap) {
        return ItemStackDataRegistry.CAPABILITIES.get(cap).predicate().test(item);
    }
}
