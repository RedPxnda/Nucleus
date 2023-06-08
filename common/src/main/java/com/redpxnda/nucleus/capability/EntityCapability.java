package com.redpxnda.nucleus.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface EntityCapability<T extends Tag> {
    T toNbt();
    void loadNbt(Tag tag);
}
