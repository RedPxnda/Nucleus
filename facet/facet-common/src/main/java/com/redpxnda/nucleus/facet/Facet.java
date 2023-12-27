package com.redpxnda.nucleus.facet;

import net.minecraft.nbt.NbtElement;

public interface Facet<T extends NbtElement> {
    T toNbt();
    void loadNbt(T nbt);
}
