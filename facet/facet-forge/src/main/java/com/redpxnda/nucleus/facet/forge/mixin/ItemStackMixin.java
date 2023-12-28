package com.redpxnda.nucleus.facet.forge.mixin;

import com.redpxnda.nucleus.facet.item.ItemStackFacet;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isEmpty();

    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/nbt/NbtCompound;)V", at = @At("RETURN"))
    private void nucleus$setupFacetsOnItemCreation(ItemConvertible item, int count, NbtCompound capNbt, CallbackInfo ci) {
        if (isEmpty()) return;
        ItemStackFacet.setupFacets((ItemStack) (Object) this);
    }
}
