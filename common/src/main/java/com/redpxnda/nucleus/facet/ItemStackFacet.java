package com.redpxnda.nucleus.facet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface ItemStackFacet<F extends ItemStackFacet<F, T>, T extends NbtElement> extends Facet<T> {
    default void updateNbtOf(FacetKey<F> key, ItemStack stack) {
        stack.getOrCreateSubNbt(FacetRegistry.TAG_FACETS_ID).put(key.id().toString(), toNbt());
    }
    default void onCopied(F original) {}

    static void setupFacets(ItemStack stack) {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.ITEM_FACET_ATTACHMENT.invoker().attach(stack, attacher);
        FacetHolder holder = FacetHolder.of(stack);
        holder.setFacetsFromAttacher(attacher);
        if (!holder.getFacets().isEmpty()) writeFacetsToNbt(stack.getOrCreateNbt(), holder);
    }

    static void writeFacetsToNbt(NbtCompound root, FacetHolder holder) {
        NbtCompound facetsNbt = new NbtCompound();
        holder.getFacets().forEach((key, facet) -> facetsNbt.put(key.id().toString(), facet.toNbt()));
        if (!facetsNbt.isEmpty())
            root.getCompound("tag").put(FacetRegistry.TAG_FACETS_ID, facetsNbt);
    }
}
