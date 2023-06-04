package com.redpxnda.nucleus.datapack.references.entity;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.item.InventoryReference;
import com.redpxnda.nucleus.datapack.references.item.ItemCooldownsReference;
import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import com.redpxnda.nucleus.datapack.references.storage.SlotAccessReference;
import com.redpxnda.nucleus.datapack.references.Statics;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import com.redpxnda.nucleus.datapack.references.storage.DamageSourceReference;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
public class PlayerReference extends LivingEntityReference<Player> {
    static { Reference.register(PlayerReference.class); }

    public PlayerReference(Player instance) {
        super(instance);
    }

    public InventoryReference getInventory() {
        return new InventoryReference(instance.getInventory());
    }

    // Generated from Player::getSlot
    public SlotAccessReference getSlot(int param0) {
        return new SlotAccessReference(instance.getSlot(param0));
    }

    // Generated from Player::getDisplayName
    public ComponentReference<?> getDisplayName() {
        return new ComponentReference<>(instance.getDisplayName());
    }

    // Generated from Player::drop
    public EntityReference<ItemEntity> drop(ItemStackReference param0, boolean param1) {
        return new EntityReference<>(instance.drop(param0.instance, param1));
    }

    // Generated from Player::drop
    public EntityReference<ItemEntity> drop(ItemStackReference param0, boolean param1, boolean param2) {
        return new EntityReference<>(instance.drop(param0.instance, param1, param2));
    }

    // Generated from Player::resetAttackStrengthTicker
    public void resetAttackStrengthTicker() {
        instance.resetAttackStrengthTicker();
    }

    // Generated from Player::hasCorrectToolForDrops
    public boolean hasCorrectToolForDrops(BlockStateReference param0) {
        return instance.hasCorrectToolForDrops(param0.instance);
    }

    public void sendSystemMessage(ComponentReference<?> component, boolean bl) {
        if (instance instanceof ServerPlayer player)
            player.sendSystemMessage(component.instance, bl);
    }

    public void sendSystemMessage(String str, boolean bl) {
        if (instance instanceof ServerPlayer player)
            player.sendSystemMessage(Component.literal(str), bl);
    }

    // Generated from Player::getDimensionChangingDelay
    public int getDimensionChangingDelay() {
        return instance.getDimensionChangingDelay();
    }

    // Generated from Player::getShoulderEntityRight
    public CompoundTagReference getShoulderEntityRight() {
        return new CompoundTagReference(instance.getShoulderEntityRight());
    }

    // Generated from Player::getShoulderEntityLeft
    public CompoundTagReference getShoulderEntityLeft() {
        return new CompoundTagReference(instance.getShoulderEntityLeft());
    }

    // Generated from Player::getAttackStrengthScale
    public float getAttackStrengthScale(float param0) {
        return instance.getAttackStrengthScale(param0);
    }

    // Generated from Player::getAbsorptionAmount
    public float getAbsorptionAmount() {
        return instance.getAbsorptionAmount();
    }

    // Generated from Player::causeFoodExhaustion
    public void causeFoodExhaustion(float param0) {
        instance.causeFoodExhaustion(param0);
    }

    // Generated from Player::displayClientMessage
    public void displayClientMessage(ComponentReference<?> param0, boolean param1) {
        instance.displayClientMessage(param0.instance, param1);
    }

    // Generated from Player::getLastDeathLocation
    // TODO (turn into gp)
    public Optional<BlockPosReference> getLastDeathLocation() {
        return instance.getLastDeathLocation().map(gp -> new BlockPosReference(gp.pos()));
    }

    // Generated from Player::setAbsorptionAmount
    public void setAbsorptionAmount(float param0) {
        instance.setAbsorptionAmount(param0);
    }

    // Generated from Player::getDestroySpeed
    public float getDestroySpeed(BlockStateReference param0) {
        return instance.getDestroySpeed(param0.instance);
    }

    // Generated from Player::stopSleepInBed
    public void stopSleepInBed(boolean param0, boolean param1) {
        instance.stopSleepInBed(param0, param1);
    }

    // Generated from Player::isSpectator
    public boolean isSpectator() {
        return instance.isSpectator();
    }

    // Generated from Player::closeContainer
    public void closeContainer() {
        instance.closeContainer();
    }

    // Generated from Player::awardStat
    public void awardStat(ResourceLocationReference param0, int param1) {
        instance.awardStat(param0.instance, param1);
    }

    // Generated from Player::awardStat
    public void awardStat(ResourceLocationReference param0) {
        instance.awardStat(param0.instance);
    }

    // Generated from Player::getItemBySlot
    public ItemStackReference getItemBySlot(Statics.EquipmentSlots param0) {
        return new ItemStackReference(instance.getItemBySlot(param0.instance));
    }

    // Generated from Player::isSwimming
    public boolean isSwimming() {
        return instance.isSwimming();
    }

    // Generated from Player::getPortalWaitTime
    public int getPortalWaitTime() {
        return instance.getPortalWaitTime();
    }

    // Generated from Player::rideTick
    public void rideTick() {
        instance.rideTick();
    }

    // Generated from Player::aiStep
    public void aiStep() {
        instance.aiStep();
    }

    // Generated from Player::getScore
    public int getScore() {
        return instance.getScore();
    }

    // Generated from Player::setScore
    public void setScore(int param0) {
        instance.setScore(param0);
    }

    // Generated from Player::die
    public void die(DamageSourceReference param0) {
        instance.die(param0.instance);
    }

    // Generated from Player::increaseScore
    public void increaseScore(int param0) {
        instance.increaseScore(param0);
    }

    // Generated from Player::mayBuild
    public boolean mayBuild() {
        return instance.mayBuild();
    }

    // Generated from Player::giveExperienceLevels
    public void giveExperienceLevels(int param0) {
        instance.giveExperienceLevels(param0);
    }

    // Generated from Player::tryToStartFallFlying
    public boolean tryToStartFallFlying() {
        return instance.tryToStartFallFlying();
    }

    // Generated from Player::giveExperiencePoints
    public void giveExperiencePoints(int param0) {
        instance.giveExperiencePoints(param0);
    }

    // Generated from Player::getXpNeededForNextLevel
    public int getXpNeededForNextLevel() {
        return instance.getXpNeededForNextLevel();
    }

    // Generated from Player::setReducedDebugInfo
    public void setReducedDebugInfo(boolean param0) {
        instance.setReducedDebugInfo(param0);
    }

    // Generated from Player::getExperienceReward
    public int getExperienceReward() {
        return instance.getExperienceReward();
    }

    // Generated from Player::setEntityOnShoulder
    public boolean setEntityOnShoulder(CompoundTagReference param0) {
        return instance.setEntityOnShoulder(param0.instance);
    }

    // Generated from Player::setRemainingFireTicks
    public void setRemainingFireTicks(int param0) {
        instance.setRemainingFireTicks(param0);
    }

    // Generated from Player::getCurrentItemAttackStrengthDelay
    public float getCurrentItemAttackStrengthDelay() {
        return instance.getCurrentItemAttackStrengthDelay();
    }

    // Generated from Player::canUseGameMasterBlocks
    public boolean canUseGameMasterBlocks() {
        return instance.canUseGameMasterBlocks();
    }

    // Generated from Player::getSleepTimer
    public int getSleepTimer() {
        return instance.getSleepTimer();
    }

    // Generated from Player::jumpFromGround
    public void jumpFromGround() {
        instance.jumpFromGround();
    }

    // Generated from Player::awardRecipesByKey
    public void awardRecipes(ResourceLocationReference[] param0) {
        instance.awardRecipesByKey(Arrays.stream(param0).map(ResourceLocationReference::instance).toArray(ResourceLocation[]::new));
    }

    // Generated from Player::causeFallDamage
    public boolean causeFallDamage(float param0, float param1, DamageSourceReference param2) {
        return instance.causeFallDamage(param0, param1, param2.instance);
    }

    // Generated from Player::stopFallFlying
    public void stopFallFlying() {
        instance.stopFallFlying();
    }

    // Generated from Player::startFallFlying
    public void startFallFlying() {
        instance.startFallFlying();
    }

    // Generated from Player::isHurt
    public boolean isHurt() {
        return instance.isHurt();
    }

    // Generated from Player::mayUseItemAt
    public boolean mayUseItemAt(BlockPosReference param0, Statics.Directions param1, ItemStackReference param2) {
        return instance.mayUseItemAt(param0.instance, param1.instance, param2.instance);
    }

    // Generated from Player::canEat
    public boolean canEat(boolean param0) {
        return instance.canEat(param0);
    }

    // Generated from Player::getEnchantmentSeed
    public int getEnchantmentSeed() {
        return instance.getEnchantmentSeed();
    }

    // Generated from Player::isCreative
    public boolean isCreative() {
        return instance.isCreative();
    }

    // Generated from Player::addItem
    public boolean addItem(ItemStackReference param0) {
        return instance.addItem(param0.instance);
    }

    // Generated from Player::getLuck
    public float getLuck() {
        return instance.getLuck();
    }

    // Generated from Player::isScoping
    public boolean isScoping() {
        return instance.isScoping();
    }

    // Generated from Player::disableShield
    public void disableShield(boolean param0) {
        instance.disableShield(param0);
    }

    // Generated from Player::canHarmPlayer
    public boolean canHarmPlayer(PlayerReference param0) {
        return instance.canHarmPlayer(param0.instance);
    }

    // Generated from Player::interactOn
    public void interactOn(EntityReference<?> param0, Statics.InteractionHands param1) {
        instance.interactOn(param0.instance, param1.instance);
    }

    // Generated from Player::attack
    public void attack(EntityReference<?> param0) {
        instance.attack(param0.instance);
    }

    // Generated from Player::removeVehicle
    public void removeVehicle() {
        instance.removeVehicle();
    }

    // Generated from Player::getSpeed
    public float getSpeed() {
        return instance.getSpeed();
    }

    // Generated from Player::sweepAttack
    public void sweepAttack() {
        instance.sweepAttack();
    }

    // Generated from Player::getCooldowns
    public ItemCooldownsReference getCooldowns() {
        return new ItemCooldownsReference(instance.getCooldowns());
    }

    // Generated from Player::magicCrit
    public void magicCrit(EntityReference<?> param0) {
        instance.magicCrit(param0.instance);
    }

    // Generated from Player::isLocalPlayer
    public boolean isLocalPlayer() {
        return instance.isLocalPlayer();
    }

    // Generated from Player::crit
    public void crit(EntityReference<?> param0) {
        instance.crit(param0.instance);
    }

    // Generated from Player::hasContainerOpen
    public boolean hasContainerOpen() {
        return instance.hasContainerOpen();
    }

    // Generated from Player::stopSleeping
    public void stopSleeping() {
        instance.stopSleeping();
    }

    // Generated from Player::startSleepInBed
    public void startSleepInBed(BlockPosReference param0) {
        instance.startSleepInBed(param0.instance);
    }
}
