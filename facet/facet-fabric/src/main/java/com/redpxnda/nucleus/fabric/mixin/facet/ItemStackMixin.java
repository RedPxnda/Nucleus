package com.redpxnda.nucleus.fabric.mixin.facet;

import com.redpxnda.nucleus.facet.ItemStackFacet;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("RETURN"))
    private void nucleus$setupFacetsOnItemCreation(ItemConvertible item, int count, CallbackInfo ci) {
        if (MiscUtil.isItemEmptyIgnoringCount((ItemStack) (Object) this)) return;
        ItemStackFacet.setupFacets((ItemStack) (Object) this);
    }
}
