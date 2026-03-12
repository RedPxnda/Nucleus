package com.redpxnda.nucleus.trinket.accessories;

import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.trinket.CommonSlotReference;
import com.redpxnda.nucleus.trinket.Trinket;
import com.redpxnda.nucleus.trinket.curio.CurioApiMirror;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

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
        trinket.getAttributeModifiers(reference1, ResourceLocation.fromNamespaceAndPath(reference1.getSlotGroupId(), reference1.getSlotId() + reference1.getSlotIndex()), stack).forEach((builder::addExclusive));
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
