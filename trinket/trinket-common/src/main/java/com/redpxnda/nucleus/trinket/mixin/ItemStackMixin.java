package com.redpxnda.nucleus.trinket.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.redpxnda.nucleus.trinket.AdditionalSlotComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyReturnValue(
            method = "is(Lnet/minecraft/tags/TagKey;)Z",
            at = @At("RETURN")
    )
    public boolean is$nucleusTrinketTagInject(boolean original, TagKey<Item> tag) {
        if (!original) {
            ItemStack stack = (ItemStack) (Object) this;
            if (stack.has(AdditionalSlotComponent.ADDITIONAL_SLOTS)) {
                return AdditionalSlotComponent.hasTagForSlot(tag, stack.get(AdditionalSlotComponent.ADDITIONAL_SLOTS), stack);
            }
        }
        return original;
    }
}
