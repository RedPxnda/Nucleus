package com.redpxnda.nucleus.capability.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class ItemStackDataRegistry {
    public static final Map<Class<? extends ItemStackCapability<?>>, Holder<?>> CAPABILITIES = new HashMap<>();
    public static final BiMap<Identifier, Class<? extends ItemStackCapability<?>>> REGISTERED = HashBiMap.create();

    public static <T extends ItemStackCapability<?>> void register(Identifier id, Predicate<ItemStack> predicate, Class<T> cap, Function<ItemStack, T> creator) {
        CAPABILITIES.put(cap, new Holder<>(id, creator, predicate));
        REGISTERED.put(id, cap);
    }

    public static Class<? extends ItemStackCapability<?>> getFromId(Identifier id) {
        return REGISTERED.get(id);
    }

    public static Identifier getIdFrom(Class<? extends ItemStackCapability<?>> cap) {
        return REGISTERED.inverse().get(cap);
    }

    public record Holder<T extends ItemStackCapability<?>>(Identifier id, Function<ItemStack, T> constructor, Predicate<ItemStack> predicate) {
        public T construct(ItemStack item) {
            return constructor.apply(item);
        }
    }

    public interface CreationListener<T extends ItemStackCapability<?>> {
        void onCreate(ItemStack entity, T cap);
    }
}
