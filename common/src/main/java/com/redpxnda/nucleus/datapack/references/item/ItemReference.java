package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.*;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import com.redpxnda.nucleus.datapack.references.storage.DamageSourceReference;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import net.minecraft.item.Item;

@SuppressWarnings("unused")
public class ItemReference<I extends Item> extends Reference<I> {
    static { Reference.register(ItemReference.class); }

    public ItemReference(I instance) {
        super(instance);
    }

    // Generated from Item::getName
    public ComponentReference<?> getName(ItemStackReference param0) {
        return new ComponentReference<>(instance.getName(param0.instance));
    }

    // Generated from Item::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from Item::use
    public void use(LevelReference param0, PlayerReference param1, Statics.InteractionHands param2) {
        instance.use(param0.instance, param1.instance, param2.instance);
    }

    // Generated from Item::isFireResistant
    public boolean isFireResistant() {
        return instance.isFireproof();
    }

    // Generated from Item::getEnchantmentValue
    public int getEnchantmentValue() {
        return instance.getEnchantability();
    }

    // Generated from Item::isCorrectToolForDrops
    public boolean isCorrectToolForDrops(BlockStateReference param0) {
        return instance.isSuitableFor(param0.instance);
    }

    // Generated from Item::interactLivingEntity
    public void interactLivingEntity(ItemStackReference param0, PlayerReference param1, LivingEntityReference<?> param2, Statics.InteractionHands param3) {
        instance.useOnEntity(param0.instance, param1.instance, param2.instance, param3.instance);
    }

/*    // Generated from Item::getDefaultAttributeModifiers
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Statics.EquipmentSlots param0) {
        return instance.getDefaultAttributeModifiers(param0.instance);
    }*/

    // Generated from Item::canAttackBlock
    public boolean canAttackBlock(BlockStateReference param0, LevelReference param1, BlockPosReference param2, PlayerReference param3) {
        return instance.canMine(param0.instance, param1.instance, param2.instance, param3.instance);
    }

    // Generated from Item::verifyTagAfterLoad
    public void verifyTagAfterLoad(CompoundTagReference param0) {
        instance.postProcessNbt(param0.instance);
    }

    // Generated from Item::isEdible
    public boolean isEdible() {
        return instance.isFood();
    }

    // Generated from Item::getFoodProperties
    public FoodPropertiesReference getFoodProperties() {
        return new FoodPropertiesReference(instance.getFoodComponent());
    }

    // Generated from Item::getDestroySpeed
    public float getDestroySpeed(ItemStackReference param0, BlockStateReference param1) {
        return instance.getMiningSpeedMultiplier(param0.instance, param1.instance);
    }

    // Generated from Item::canBeDepleted
    public boolean canBeDepleted() {
        return instance.isDamageable();
    }

    // Generated from Item::hurtEnemy
    public boolean hurtEnemy(ItemStackReference param0, LivingEntityReference<?> param1, LivingEntityReference<?> param2) {
        return instance.postHit(param0.instance, param1.instance, param2.instance);
    }

    // Generated from Item::getMaxDamage
    public int getMaxDamage() {
        return instance.getMaxDamage();
    }

    // Generated from Item::getDescriptionId
    public String getDescriptionId(ItemStackReference param0) {
        return instance.getTranslationKey(param0.instance);
    }

    // Generated from Item::getDescriptionId
    public String getDescriptionId() {
        return instance.getTranslationKey();
    }

    // Generated from Item::mineBlock
    public boolean mineBlock(ItemStackReference param0, LevelReference param1, BlockStateReference param2, BlockPosReference param3, LivingEntityReference<?> param4) {
        return instance.postMine(param0.instance, param1.instance, param2.instance, param3.instance, param4.instance);
    }

    // Generated from Item::getDescription
    public ComponentReference<?> getDescription() {
        return new ComponentReference<>(instance.getName());
    }

    // Generated from Item::getMaxStackSize
    public int getMaxStackSize() {
        return instance.getMaxCount();
    }

    // Generated from Item::isFoil
    public boolean isFoil(ItemStackReference param0) {
        return instance.hasGlint(param0.instance);
    }

    // Generated from Item::getUseAnimation
    public Statics.UseAnims getUseAnimation(ItemStackReference param0) {
        return Statics.UseAnims.get(instance.getUseAction(param0.instance));
    }

    // Generated from Item::getRarity
    public Statics.Rarities getRarity(ItemStackReference param0) {
        return Statics.Rarities.get(instance.getRarity(param0.instance));
    }

    // Generated from Item::isEnchantable
    public boolean isEnchantable(ItemStackReference param0) {
        return instance.isEnchantable(param0.instance);
    }

    // Generated from Item::isValidRepairItem
    public boolean isValidRepairItem(ItemStackReference param0, ItemStackReference param1) {
        return instance.canRepair(param0.instance, param1.instance);
    }

    // Generated from Item::getDefaultInstance
    public ItemStackReference getDefaultInstance() {
        return new ItemStackReference(instance.getDefaultStack());
    }

    // Generated from Item::canBeHurtBy
    public boolean canBeHurtBy(DamageSourceReference param0) {
        return instance.damage(param0.instance);
    }
}
