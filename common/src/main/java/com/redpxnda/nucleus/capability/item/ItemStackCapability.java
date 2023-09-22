package com.redpxnda.nucleus.capability.item;

import net.minecraft.nbt.NbtElement;

public interface ItemStackCapability<T extends NbtElement> {
    T toNbt();
    void loadNbt(T tag);

    /**
     * MUST RETURN SAME TYPE AS CALLER
     */
    <E extends ItemStackCapability<T>> E createCopy();
}
