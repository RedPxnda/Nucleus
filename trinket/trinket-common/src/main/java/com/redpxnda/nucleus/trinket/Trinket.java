package com.redpxnda.nucleus.trinket;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * A simple class for abstracting trinket behavior to be compatible for both Trinkets and Curios.
 */
@SuppressWarnings("unused")
public interface Trinket {
    /**
     * Called every tick on the client and server side
     *
     * @param stack The stack being ticked
     * @param entity The entity wearing the stack
     * @param slotIndex The index of the slot
     */
    default void tick(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
    }

    /**
     * Called when an entity equips a trinket
     *
     * @param stack The stack being equipped
     * @param entity The entity that equipped the stack
     */
    default void onEquip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
    }

    /**
     * Called when an entity equips a trinket
     *
     * @param stack The stack being unequipped
     * @param entity The entity that unequipped the stack
     */
    default void onUnequip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
    }

    /**
     * Determines whether an entity can equip a trinket
     *
     * @param stack The stack being equipped
     * @param entity The entity that is equipping the stack
     * @return Whether the stack can be equipped
     */
    default boolean canEquip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
        return true;
    }

    /**
     * Determines whether an entity can unequip a trinket
     *
     * @param stack The stack being unequipped
     * @param entity The entity that is unequipping the stack
     * @return Whether the stack can be unequipped
     */
    default boolean canUnequip(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex) {
        return true;
    }

    /**
     * Determines whether this trinket should overwrite (return false) or extend (return true)
     * the default attribute modifier behavior. (Default behavior being nbt based attribute modifiers)
     * This essentially determines whether the super should be called.
     * <p></p>
     * NOTE: might not work on forge for curios, untested
     * <p></p>
     * @param stack The stack holding the potential attributes
     * @param entity The entity wearing the ItemStack
     * @param uuid The generated UUID for use in attribute modifiers (See ICurioItem's and Trinket's getAttributeModifiers, they explain it better)
     * @return whether this trinket should extend default behavior attribute modifier behavior
     */
    default boolean useNbtAttributeBehavior(ItemStack stack, LivingEntity entity, CommonSlotReference slotIndex, UUID uuid) {
        return true;
    }

    default DropRule getDropRule(ItemStack stack, LivingEntity entity, int slotIndex) {
        return DropRule.DEFAULT;
    }

    enum DropRule {
        KEEP, DROP, DESTROY, DEFAULT;
    }
}
