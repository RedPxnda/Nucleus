package com.redpxnda.nucleus.mixin.facet;

import com.redpxnda.nucleus.facet.*;
import com.redpxnda.nucleus.util.MiscUtil;
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
public abstract class ItemStackMixin implements FacetHolder {
    @Unique
    private final FacetInventory nucleus$facets = new FacetInventory();
    
    @Override
    public FacetInventory getFacets() {
        return nucleus$facets;
    }

    // moved to forge/fabric respectively
    /*@Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("RETURN"))
    private void nucleus$setupFacetsOnItemCreation(ItemConvertible item, int count, CallbackInfo ci) {
        if (MiscUtil.isItemEmptyIgnoringCount((ItemStack) (Object) this)) return;
        ItemStackFacet.setupFacets((ItemStack) (Object) this);
    }*/

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void nucleus$saveFacets(NbtCompound root, CallbackInfoReturnable<NbtCompound> cir) {
        if (MiscUtil.isItemEmptyIgnoringCount((ItemStack) (Object) this)) return;
        // if (!root.contains("tag")) root.put("tag", new NbtCompound());
        ItemStackFacet.writeFacetsToNbt(root, this);
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("RETURN"))
    private void nucleus$readFacets(NbtCompound nbt, CallbackInfo ci) {
        if (MiscUtil.isItemEmptyIgnoringCount((ItemStack) (Object) this)) return;
        ItemStackFacet.setupFacets((ItemStack) (Object) this);
        nucleus$loadTagFacetData(nbt.getCompound("tag"));
    }

    @Inject(method = "setNbt", at = @At("TAIL"))
    private void nucleus$reloadFacets(NbtCompound nbt, CallbackInfo ci) {
        if (MiscUtil.isItemEmptyIgnoringCount((ItemStack) (Object) this)) return;
        //nucleus$setupFacets();
        nucleus$loadTagFacetData(nbt == null ? new NbtCompound() : nbt);
    }

    @Inject(method = "copy", at = @At("TAIL"))
    private void nucleus$copyFacets(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack newStack = cir.getReturnValue();
        //if (MiscUtil.isItemEmptyIgnoringCount((ItemStack) (Object) this)) return;
        FacetInventory inv = FacetHolder.of(newStack).getFacets();
        inv.forEach((key, facet) -> {
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
}
