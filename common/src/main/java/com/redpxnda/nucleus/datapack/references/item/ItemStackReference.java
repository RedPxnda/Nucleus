package com.redpxnda.nucleus.datapack.references.item;

import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.datapack.references.*;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import com.redpxnda.nucleus.datapack.references.tag.ListTagReference;
import com.redpxnda.nucleus.datapack.references.tag.TagReference;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

@SuppressWarnings("unused")
public class ItemStackReference extends Reference<ItemStack> {
    static { Reference.register(ItemStackReference.class); }

    public ItemStackReference(ItemStack instance) {
        super(instance);
    }

    // Generated from ItemStack::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from ItemStack::isEmpty
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    // Generated from ItemStack::split
    public ItemStackReference split(int param0) {
        return new ItemStackReference(instance.split(param0));
    }

    // Generated from ItemStack::save
    public CompoundTagReference save(CompoundTagReference param0) {
        return new CompoundTagReference(instance.writeNbt(param0.instance));
    }

    // Generated from ItemStack::copy
    public ItemStackReference copy() {
        return new ItemStackReference(instance.copy());
    }

//    // Generated from ItemStack::is
//    public boolean is(TagKey param0) {
//        return instance.is(param0);
//    }

    // Generated from ItemStack::is
    public boolean is(ItemReference<?> param0) {
        return instance.isOf(param0.instance);
    }

    // Generated from ItemStack::grow
    public void grow(int param0) {
        instance.increment(param0);
    }

    // Generated from ItemStack::getCount
    public int getCount() {
        return instance.getCount();
    }

    // Generated from ItemStack::getItem
    public ItemReference<?> getItem() {
        return new ItemReference<>(instance.getItem());
    }

    // Generated from ItemStack::getTag
    public CompoundTagReference getTag() {
        return new CompoundTagReference(instance.getNbt());
    }

    // Generated from ItemStack::use
    public void use(LevelReference param0, PlayerReference param1, Statics.InteractionHands param2) {
        instance.use(param0.instance, param1.instance, param2.instance);
    }

    // Generated from ItemStack::getDisplayName
    public ComponentReference<?> getDisplayName() {
        return new ComponentReference<>(instance.toHoverableText());
    }

    // Generated from ItemStack::setTag
    public void setTag(CompoundTagReference param0) {
        instance.setNbt(param0.instance);
    }

    // Generated from ItemStack::getDamageValue
    public int getDamageValue() {
        return instance.getDamage();
    }

    // Generated from ItemStack::setDamageValue
    public void setDamageValue(int param0) {
        instance.setDamage(param0);
    }

    // Generated from ItemStack::isCorrectToolForDrops
    public boolean isCorrectToolForDrops(BlockStateReference param0) {
        return instance.isSuitableFor(param0.instance);
    }

    // Generated from ItemStack::interactLivingEntity
    public void interactLivingEntity(PlayerReference param0, LivingEntityReference<?> param1, Statics.InteractionHands param2) {
        instance.useOnEntity(param0.instance, param1.instance, param2.instance);
    }

    // Generated from ItemStack::isDamageableItem
    public boolean isDamageableItem() {
        return instance.isDamageable();
    }

    // Generated from ItemStack::getDestroySpeed
    public float getDestroySpeed(BlockStateReference param0) {
        return instance.getMiningSpeedMultiplier(param0.instance);
    }

    // Generated from ItemStack::getMaxStackSize
    public int getMaxStackSize() {
        return instance.getMaxCount();
    }

    // Generated from ItemStack::isStackable
    public boolean isStackable() {
        return instance.isStackable();
    }

    // Generated from ItemStack::setCount
    public void setCount(int param0) {
        instance.setCount(param0);
    }

    // Generated from ItemStack::shrink
    public void shrink(int param0) {
        instance.decrement(param0);
    }

    // Generated from ItemStack::isDamaged
    public boolean isDamaged() {
        return instance.isDamaged();
    }

    // Generated from ItemStack::getMaxDamage
    public int getMaxDamage() {
        return instance.getMaxDamage();
    }

    // Generated from ItemStack::getOrCreateTag
    public CompoundTagReference getOrCreateTag() {
        return new CompoundTagReference(instance.getOrCreateNbt());
    }

    // Generated from ItemStack::sameItem
    public boolean sameItem(ItemStackReference param0) {
        return ItemStack.areItemsEqual(this.instance, param0.instance);
    }

    // Generated from ItemStack::setPopTime
    public void setPopTime(int param0) {
        instance.setBobbingAnimationTime(param0);
    }

    // Generated from ItemStack::getDescriptionId
    public String getDescriptionId() {
        return instance.getTranslationKey();
    }

    // Generated from ItemStack::getUseDuration
    public int getUseDuration() {
        return instance.getMaxUseTime();
    }

    // Generated from ItemStack::getUseAnimation
    public Statics.UseAnims getUseAnimation() {
        return Statics.UseAnims.get(instance.getUseAction());
    }

    // Generated from ItemStack::getPopTime
    public int getPopTime() {
        return instance.getBobbingAnimationTime();
    }

    // Generated from ItemStack::mineBlock
    public void mineBlock(LevelReference param0, BlockStateReference param1, BlockPosReference param2, PlayerReference param3) {
        instance.postMine(param0.instance, param1.instance, param2.instance, param3.instance);
    }

    // Generated from ItemStack::releaseUsing
    public void releaseUsing(LevelReference param0, LivingEntityReference<?> param1, int param2) {
        instance.onStoppedUsing(param0.instance, param1.instance, param2);
    }

    // Generated from ItemStack::hurtEnemy
    public void hurtEnemy(LivingEntityReference<?> param0, PlayerReference param1) {
        instance.postHit(param0.instance, param1.instance);
    }

    // Generated from ItemStack::getOrCreateTagElement
    public CompoundTagReference getOrCreateTagElement(String param0) {
        return new CompoundTagReference(instance.getOrCreateSubNbt(param0));
    }

    // Generated from ItemStack::getAttributeModifiers
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(Statics.EquipmentSlots param0) {
        return instance.getAttributeModifiers(param0.instance);
    }

    // Generated from ItemStack::hasFoil
    public boolean hasFoil() {
        return instance.hasGlint();
    }

    // Generated from ItemStack::isEnchantable
    public boolean isEnchantable() {
        return instance.isEnchantable();
    }

    // Generated from ItemStack::hasCustomHoverName
    public boolean hasCustomHoverName() {
        return instance.hasCustomName();
    }

    // Generated from ItemStack::hasTag
    public boolean hasTag() {
        return instance.hasNbt();
    }

    // Generated from ItemStack::removeTagKey
    public void removeTagKey(String param0) {
        instance.removeSubNbt(param0);
    }

    // Generated from ItemStack::resetHoverName
    public void resetHoverName() {
        instance.removeCustomName();
    }

    // Generated from ItemStack::getEnchantmentTags
    public ListTagReference getEnchantmentTags() {
        return new ListTagReference(instance.getEnchantments());
    }

    // Generated from ItemStack::getTagElement
    public CompoundTagReference getTagElement(String param0) {
        return new CompoundTagReference(instance.getSubNbt(param0));
    }

    // Generated from ItemStack::getRarity
    public Statics.Rarities getRarity() {
        return Statics.Rarities.get(instance.getRarity());
    }

    // Generated from ItemStack::addTagElement
    public void addTagElement(String param0, TagReference<?> param1) {
        instance.setSubNbt(param0, param1.instance);
    }

//    // Generated from ItemStack::addAttributeModifier
//    public void addAttributeModifier(Attribute param0, AttributeModifier param1, Enums.EquipmentSlots param2) {
//        instance.addAttributeModifier(param0, param1, param2.instance);
//    }

//    // Generated from ItemStack::enchant
//    public void enchant(Enchantment param0, int param1) {
//        instance.enchant(param0, param1);
//    }

    public void enchant(ResourceLocationReference enchantmentLoc, int param1) {
        Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentLoc.instance);
        if (enchantment != null)
            instance.addEnchantment(enchantment, param1);
    }

    // Generated from ItemStack::isFramed
    public boolean isFramed() {
        return instance.isInFrame();
    }

    // Generated from ItemStack::setRepairCost
    public void setRepairCost(int param0) {
        instance.setRepairCost(param0);
    }

    // Generated from ItemStack::getBaseRepairCost
    public int getBaseRepairCost() {
        return instance.getRepairCost();
    }

    // Generated from ItemStack::getFrame
    public EntityReference<ItemFrameEntity> getFrame() {
        return new EntityReference<>(instance.getFrame());
    }

    // Generated from ItemStack::isEnchanted
    public boolean isEnchanted() {
        return instance.hasEnchantments();
    }

    // Generated from ItemStack::isEdible
    public boolean isEdible() {
        return instance.isFood();
    }
}
