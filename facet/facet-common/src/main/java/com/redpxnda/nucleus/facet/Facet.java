package com.redpxnda.nucleus.facet;

import net.minecraft.nbt.Tag;

public interface Facet<T extends Tag> {
    default boolean shouldSave() {
        return true;
    }

    T toNbt();

    void loadNbt(T nbt);
}
