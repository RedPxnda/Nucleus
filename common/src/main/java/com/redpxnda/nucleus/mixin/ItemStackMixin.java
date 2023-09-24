package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.facet.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin implements FacetHolder {
    @Unique
    private final FacetInventory nucleus$facets = new FacetInventory();
    
    @Override
    public FacetInventory getFacets() {
        return nucleus$facets;
    }

    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("RETURN"))
    private void nucleus$setupFacetsOnItemCreation(ItemConvertible item, int count, CallbackInfo ci) {
        nucleus$setupFacets();
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void nucleus$saveFacets(NbtCompound root, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound facetsNbt = new NbtCompound();
        nucleus$facets.forEach((key, facet) -> facetsNbt.put(key.id().toString(), facet.toNbt()));

        boolean shouldPlace = !facetsNbt.isEmpty();
        boolean hasTag = root.contains("tag");

        if (shouldPlace) {
            if (!hasTag) root.put("tag", new NbtCompound());
            root.getCompound("tag").put(FacetRegistry.TAG_FACETS_ID, facetsNbt);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("RETURN"))
    private void nucleus$readFacets(NbtCompound nbt, CallbackInfo ci) {
        nucleus$setupFacets();
        nucleus$loadTagFacetData(nbt.getCompound("tag"));
    }

    @Inject(method = "setNbt", at = @At("TAIL"))
    private void nucleus$reloadFacets(NbtCompound nbt, CallbackInfo ci) {
        nucleus$loadTagFacetData(nbt);
    }

    @Inject(method = "copy", at = @At("TAIL"))
    private void nucleus$copyFacets(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack newStack = cir.getReturnValue();
        FacetHolder.of(newStack).getFacets().forEach((key, facet) -> {
            if (facet instanceof ItemStackFacet isf) {
                Facet<?> old = nucleus$facets.get(key);
                if (old != null) isf.onCopied((ItemStackFacet) old);
            }
        });
    }

    private void nucleus$loadTagFacetData(NbtCompound tag) {
        if (tag.contains(FacetRegistry.TAG_FACETS_ID)) {
            NbtCompound facets = tag.getCompound(FacetRegistry.TAG_FACETS_ID);
            nucleus$facets.forEach((key, facet) -> {
                NbtElement element = facets.get(key.id().toString());
                FacetRegistry.loadNbtToFacet(element, key, facet);
            });
        }
    }

    private void nucleus$setupFacets() {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.ITEM_FACET_ATTACHMENT.invoker().attach((ItemStack) (Object) this, attacher);
        setFacetsFromAttacher(attacher);
    }
}
