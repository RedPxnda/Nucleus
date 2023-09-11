package com.redpxnda.nucleus.capability.entity;

import net.minecraft.nbt.Tag;

public interface EntityCapability<T extends Tag> {
    T toNbt();
    void loadNbt(T tag);
}
