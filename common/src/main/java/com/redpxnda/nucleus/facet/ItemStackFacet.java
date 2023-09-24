package com.redpxnda.nucleus.facet;

import net.minecraft.nbt.NbtElement;

public interface ItemStackFacet<F extends ItemStackFacet<F, T>, T extends NbtElement> extends Facet<T> {
    default void onCopied(F original) {}
}
