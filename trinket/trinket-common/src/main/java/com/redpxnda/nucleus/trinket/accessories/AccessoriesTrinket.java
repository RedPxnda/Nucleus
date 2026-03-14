package com.redpxnda.nucleus.trinket.accessories;

import com.redpxnda.nucleus.trinket.CommonSlotReference;
import com.redpxnda.nucleus.trinket.Trinket;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AccessoriesTrinket implements Accessory {

    protected final Trinket trinket;

    public AccessoriesTrinket(Trinket trinket) {
        this.trinket = trinket;
    }

    protected LivingEntity entity(SlotReference reference) {
        return reference.entity();
    }

    /* ---------------- tick ---------------- */

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        trinket.tick(stack, entity(reference), AccessoriesApiMirror.convert(reference));
    }

    /* ---------------- equip ---------------- */

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        trinket.onEquip(stack, entity(reference), AccessoriesApiMirror.convert(reference));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        trinket.onUnequip(stack, entity(reference), AccessoriesApiMirror.convert(reference));
    }

    /* ---------------- equip rules ---------------- */

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return trinket.canEquip(stack, entity(reference), AccessoriesApiMirror.convert(reference));
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        return trinket.canUnequip(stack, entity(reference), AccessoriesApiMirror.convert(reference));
    }

    @Override
    public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        CommonSlotReference reference1 = AccessoriesApiMirror.convert(reference);
        trinket.getAttributeModifiers(reference1, id(reference.slotName()), stack).forEach((builder::addExclusive));
    }

    public static ResourceLocation id(String string) {
        String[] parts = string.split(":");
        if (parts.length > 1) {
            return ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
        }
        return ResourceLocation.fromNamespaceAndPath("accessories", string);
    }

    /* ---------------- drop rule ---------------- */

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        Trinket.DropRule rule =
                trinket.getDropRule(stack, entity(reference), reference.slot());

        return switch (rule) {
            case KEEP -> DropRule.KEEP;
            case DROP -> DropRule.DROP;
            case DESTROY -> DropRule.DESTROY;
            case DEFAULT -> DropRule.DEFAULT;
        };
    }
}
