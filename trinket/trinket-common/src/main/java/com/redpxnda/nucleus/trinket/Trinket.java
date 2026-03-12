package com.redpxnda.nucleus.trinket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

/**
 * A simple class for abstracting trinket behavior to be compatible for both Trinkets and Curios.
 */
@SuppressWarnings("unused")
public interface Trinket {
    /**
     * Called every tick on the client and server side
     *
     * @param stack     The stack being ticked
     * @param entity    The entity wearing the stack
     * @param slotIndex The index of the slot
     */
    default void tick(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
    }

    /**
     * Called when an entity equips a trinket
     *
     * @param stack  The stack being equipped
     * @param entity The entity that equipped the stack
     */
    default void onEquip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
    }

    /**
     * Called when an entity equips a trinket
     *
     * @param stack  The stack being unequipped
     * @param entity The entity that unequipped the stack
     */
    default void onUnequip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
    }

    /**
     * Determines whether an entity can equip a trinket
     *
     * @param stack  The stack being equipped
     * @param entity The entity that is equipping the stack
     * @return Whether the stack can be equipped
     */
    default boolean canEquip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
        return true;
    }

    /**
     * Determines whether an entity can unequip a trinket
     *
     * @param stack  The stack being unequipped
     * @param entity The entity that is unequipping the stack
     * @return Whether the stack can be unequipped
     */
    default boolean canUnequip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
        return true;
    }

    /**
     * @param ctx   the slot in question
     * @param id    a potential attribute id to be used
     * @param stack the itemstack in question
     * @return
     */
    default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            CommonSlotReference ctx, ResourceLocation id, ItemStack stack) {
        return ArrayListMultimap.create();
    }

    default DropRule getDropRule(ItemStack stack, LivingEntity entity, int slotIndex) {
        return DropRule.DEFAULT;
    }

    enum DropRule {
        KEEP, DROP, DESTROY, DEFAULT;
    }
}
