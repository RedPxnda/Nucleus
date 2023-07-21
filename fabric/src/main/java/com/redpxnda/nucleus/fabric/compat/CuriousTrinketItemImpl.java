package com.redpxnda.nucleus.fabric.compat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.compat.CuriousTrinket;
import com.redpxnda.nucleus.compat.CuriousTrinketItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CuriousTrinketItemImpl extends TrinketItem implements CuriousTrinket {
    protected final CuriousTrinketItem trinket;

    public CuriousTrinketItemImpl(CuriousTrinketItem trinket, Properties settings) {
        super(settings);
        this.trinket = trinket;
    }

    @Override
    public void tick(ItemStack stack, LivingEntity entity) {
        trinket.tick(stack, entity);
    }

    @Override
    public void onEquip(ItemStack stack, LivingEntity entity) {
        trinket.onEquip(stack, entity);
    }

    @Override
    public void onUnequip(ItemStack stack, LivingEntity entity) {
        trinket.onUnequip(stack, entity);
    }

    @Override
    public boolean canEquip(ItemStack stack, LivingEntity entity) {
        return trinket.canEquip(stack, entity);
    }

    @Override
    public boolean canUnequip(ItemStack stack, LivingEntity entity) {
        return trinket.canUnequip(stack, entity);
    }

    @Override
    public boolean useNbtAttributeBehavior(ItemStack stack, LivingEntity entity, UUID uuid) {
        return trinket.useNbtAttributeBehavior(stack, entity, uuid);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, LivingEntity entity, UUID uuid) {
        return trinket.getAttributeModifiers(stack, entity, uuid);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        trinket.tick(stack, entity);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        trinket.onEquip(stack, entity);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        trinket.onEquip(stack, entity);
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return trinket.canUnequip(stack, entity);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return trinket.canUnequip(stack, entity);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<Attribute, AttributeModifier> modifiers = trinket.useNbtAttributeBehavior(stack, entity, uuid) ?
                super.getModifiers(stack, slot, entity, uuid) :
                HashMultimap.create();

        modifiers.putAll(trinket.getAttributeModifiers(stack, entity, uuid));

        return modifiers;
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onBreak(stack, slot, entity);
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return super.getDropRule(stack, slot, entity);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        trinket.onUseTick(level, livingEntity, itemStack, i);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        trinket.onDestroyed(itemEntity);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag compoundTag) {
        trinket.verifyTagAfterLoad(compoundTag);
    }

    @Override
    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return trinket.canAttackBlock(blockState, level, blockPos, player);
    }

    @Override
    public Item asItem() {
        return trinket.asItem();
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        return trinket.useOn(useOnContext);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return trinket.getDestroySpeed(itemStack, blockState);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        return trinket.finishUsingItem(itemStack, level, livingEntity);
    }

    @Override
    public boolean canBeDepleted() {
        return trinket.canBeDepleted();
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return trinket.isBarVisible(itemStack);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return trinket.getBarWidth(itemStack);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return trinket.getBarColor(itemStack);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        return trinket.overrideStackedOnOther(itemStack, slot, clickAction, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack itemStack, ItemStack itemStack2, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        return trinket.overrideOtherStackedOnMe(itemStack, itemStack2, slot, clickAction, player, slotAccess);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
        return trinket.hurtEnemy(itemStack, livingEntity, livingEntity2);
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        return trinket.mineBlock(itemStack, level, blockState, blockPos, livingEntity);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockState) {
        return trinket.isCorrectToolForDrops(blockState);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        return trinket.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }

    @Override
    public Component getDescription() {
        return trinket.getDescription();
    }

    @Override
    public String toString() {
        return trinket.toString();
    }

    @Override
    public String getDescriptionId() {
        return trinket.getDescriptionId();
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return trinket.getDescriptionId(itemStack);
    }

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return trinket.shouldOverrideMultiplayerNbt();
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return trinket.hasCraftingRemainingItem();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        trinket.inventoryTick(itemStack, level, entity, i, bl);
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
        trinket.onCraftedBy(itemStack, level, player);
    }

    @Override
    public boolean isComplex() {
        return trinket.isComplex();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return trinket.getUseAnimation(itemStack);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return trinket.getUseDuration(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        trinket.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        trinket.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return trinket.getTooltipImage(itemStack);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return trinket.getName(itemStack);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return trinket.isFoil(itemStack);
    }

    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return trinket.getRarity(itemStack);
    }

    @Override
    public boolean isEnchantable(ItemStack itemStack) {
        return trinket.isEnchantable(itemStack);
    }

    @Override
    public int getEnchantmentValue() {
        return trinket.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return trinket.isValidRepairItem(itemStack, itemStack2);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return trinket.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean useOnRelease(ItemStack itemStack) {
        return trinket.useOnRelease(itemStack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return trinket.getDefaultInstance();
    }

    @Override
    public boolean isEdible() {
        return trinket.isEdible();
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties() {
        return trinket.getFoodProperties();
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return trinket.getDrinkingSound();
    }

    @Override
    public SoundEvent getEatingSound() {
        return trinket.getEatingSound();
    }

    @Override
    public boolean isFireResistant() {
        return trinket.isFireResistant();
    }

    @Override
    public boolean canBeHurtBy(DamageSource damageSource) {
        return trinket.canBeHurtBy(damageSource);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return trinket.canFitInsideContainerItems();
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return trinket.requiredFeatures();
    }
}
