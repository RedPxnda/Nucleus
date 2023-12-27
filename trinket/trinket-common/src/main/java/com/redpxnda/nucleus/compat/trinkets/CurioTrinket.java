package com.redpxnda.nucleus.compat.trinkets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * A simple class for abstracting trinket behavior to be compatible for both Trinkets and Curios.
 */
public interface CurioTrinket {
    /**
     * Called every tick on the client and server side
     *
     * @param stack The stack being ticked
     * @param entity The entity wearing the stack
     * @param slotIndex The index of the slot
     */
    default void tick(ItemStack stack, LivingEntity entity, int slotIndex) {
    }

    /**
     * Called when an entity equips a trinket
     *
     * @param stack The stack being equipped
     * @param entity The entity that equipped the stack
     */
    default void onEquip(ItemStack stack, LivingEntity entity, int slotIndex) {
    }

    /**
     * Called when an entity equips a trinket
     *
     * @param stack The stack being unequipped
     * @param entity The entity that unequipped the stack
     */
    default void onUnequip(ItemStack stack, LivingEntity entity, int slotIndex) {
    }

    /**
     * Determines whether an entity can equip a trinket
     *
     * @param stack The stack being equipped
     * @param entity The entity that is equipping the stack
     * @return Whether the stack can be equipped
     */
    default boolean canEquip(ItemStack stack, LivingEntity entity, int slotIndex) {
        return true;
    }

    /**
     * Determines whether an entity can unequip a trinket
     *
     * @param stack The stack being unequipped
     * @param entity The entity that is unequipping the stack
     * @return Whether the stack can be unequipped
     */
    default boolean canUnequip(ItemStack stack, LivingEntity entity, int slotIndex) {
        return !EnchantmentHelper.hasBindingCurse(stack);
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
    default boolean useNbtAttributeBehavior(ItemStack stack, LivingEntity entity, int slotIndex, UUID uuid) {
        return true;
    }

    /**
     * The attribute modifiers this trinket should provide.
     * Override {@link #useNbtAttributeBehavior(ItemStack, LivingEntity, int, UUID)} to control whether attributes can be added with an nbt tag.
     *
     * @param stack The stack holding the potential attributes
     * @param entity The entity wearing the ItemStack
     * @param uuid The generated UUID for use in attribute modifiers (See ICurioItem's and Trinket's getAttributeModifiers, they explain it better)
     * @return A multimap holding every attribute modifier that should be applied when wearing this trinket.
     */
    default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, LivingEntity entity, int slotIndex, UUID uuid) {
        return HashMultimap.create();
    }

    default DropRule getDropRule(ItemStack stack, LivingEntity entity, int slotIndex) {
        return DropRule.DEFAULT;
    }

    enum DropRule {
        KEEP, DROP, DESTROY, DEFAULT;
    }
}
