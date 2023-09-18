package com.redpxnda.nucleus.capability.entity;

import net.minecraft.nbt.NbtElement;

public interface EntityCapability<T extends NbtElement> {
    T toNbt();
    void loadNbt(T tag);
}
