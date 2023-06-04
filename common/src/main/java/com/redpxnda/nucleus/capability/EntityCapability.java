package com.redpxnda.nucleus.capability;

import net.minecraft.nbt.CompoundTag;

public interface EntityCapability {
    CompoundTag toNbt();
    void loadNbt(CompoundTag tag);
}
