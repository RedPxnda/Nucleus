package com.redpxnda.nucleus.datapack.references.entity;

import com.mojang.datafixers.util.Pair;
import com.redpxnda.nucleus.datapack.references.LevelReference;
import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.Statics;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.effect.MobEffectInstanceReference;
import com.redpxnda.nucleus.datapack.references.item.ItemReference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.datapack.references.storage.DamageSourceReference;
import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import com.redpxnda.nucleus.datapack.references.storage.SlotAccessReference;
import com.redpxnda.nucleus.datapack.references.storage.TargetingConditionsReference;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import org.luaj.vm2.LuaFunction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.redpxnda.nucleus.util.MiscUtil.getMobEffect;

@SuppressWarnings("unused")
public class LivingEntityReference<E extends LivingEntity> extends EntityReference<E> {
    static { Reference.register(LivingEntityReference.class); }

    public LivingEntityReference(E instance) {
        super(instance);
    }

    // Generated from LivingEntity::isAlive
    public boolean isAlive() {
        return instance.isAlive();
    }

    // Generated from LivingEntity::getSlot
    public SlotAccessReference getSlot(int param0) {
        return new SlotAccessReference(instance.getStackReference(param0));
    }
/* TODO
    // Generated from LivingEntity::getAttributes
    public AttributeMap getAttributes() {
        return instance.getAttributes();
    }
    // Generated from LivingEntity::getAttribute
    public AttributeInstance getAttribute(Attribute param0) {
        return instance.getAttribute(param0);
    }
    // Generated from LivingEntity::getAttributeBaseValue
    public double getAttributeBaseValue(Attribute param0) {
        return instance.getAttributeBaseValue(param0);
    }
    // Generated from LivingEntity::getAttributeValue
    public double getAttributeValue(Attribute param0) {
        return instance.getAttributeValue(param0);
    }
 */

    // Generated from LivingEntity::push
    public void push(EntityReference<?> param0) {
        instance.pushAwayFrom(param0.instance);
    }

    // Generated from LivingEntity::canSpawnSoulSpeedParticle
    public boolean canSpawnSoulSpeedParticle() {
        return instance.shouldDisplaySoulSpeedEffects();
    }

    // Generated from LivingEntity::getLastHurtByMobTimestamp
    public int getLastHurtByMobTimestamp() {
        return instance.getLastAttackedTime();
    }

    // Generated from LivingEntity::setLastHurtByPlayer
    public void setLastHurtByPlayer(PlayerReference param0) {
        instance.setAttacking(param0.instance);
    }

    // Generated from LivingEntity::shouldDropExperience
    public boolean shouldDropExperience() {
        return instance.shouldDropXp();
    }

    // Generated from LivingEntity::getLastHurtMobTimestamp
    public int getLastHurtMobTimestamp() {
        return instance.getLastAttackTime();
    }

    // Generated from LivingEntity::getExperienceReward
    public int getExperienceReward() {
        return instance.getXpToDrop();
    }

    // Generated from LivingEntity::shouldDiscardFriction
    public boolean shouldDiscardFriction() {
        return instance.hasNoDrag();
    }

    // Generated from LivingEntity::getAbsorptionAmount
    public float getAbsorptionAmount() {
        return instance.getAbsorptionAmount();
    }

    // Generated from LivingEntity::canBreatheUnderwater
    public boolean canBreatheUnderwater() {
        return instance.canBreatheInWater();
    }

    // Generated from LivingEntity::isDamageSourceBlocked
    public boolean isDamageSourceBlocked(DamageSourceReference param0) {
        return instance.blockedByShield(param0.instance);
    }

    // Generated from LivingEntity::removeEffectNoUpdate
    public MobEffectInstanceReference removeEffectNoUpdate(ResourceLocationReference param0) {
        return new MobEffectInstanceReference(instance.removeStatusEffectInternal(getMobEffect(param0)));
    }
    // Generated from LivingEntity::getActiveEffectsMap
    public Map<ResourceLocationReference, MobEffectInstanceReference> getActiveEffectsMap() {
        return instance.getActiveStatusEffects().entrySet().stream().map(e -> Pair.of(
                new ResourceLocationReference(Registries.STATUS_EFFECT.getId(e.getKey())),
                new MobEffectInstanceReference(e.getValue())
                )).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }
    // Generated from LivingEntity::getEffect
    public MobEffectInstanceReference getEffect(ResourceLocationReference param0) {
        return new MobEffectInstanceReference(instance.getStatusEffect(getMobEffect(param0)));
    }
    // Generated from LivingEntity::hasEffect
    public boolean hasEffect(ResourceLocationReference param0) {
        return instance.hasStatusEffect(getMobEffect(param0));
    }
    // Generated from LivingEntity::addEffect
    public boolean addEffect(MobEffectInstanceReference param0) {
        return instance.addStatusEffect(param0.instance);
    }
    // Generated from LivingEntity::canBeAffected
    public boolean canBeAffected(MobEffectInstanceReference param0) {
        return instance.canHaveStatusEffect(param0.instance);
    }
    // Generated from LivingEntity::removeEffect
    public boolean removeEffect(ResourceLocationReference param0) {
        return instance.removeStatusEffect(getMobEffect(param0));
    }
    // Generated from LivingEntity::getActiveEffects
    public List<MobEffectInstanceReference> getActiveEffects() {
        return instance.getStatusEffects().stream().map(MobEffectInstanceReference::new).toList();
    }

    // Generated from LivingEntity::getVisibilityPercent
    public double getVisibilityPercent(EntityReference<?> param0) {
        return instance.getAttackDistanceScalingFactor(param0.instance);
    }

    // Generated from LivingEntity::isInvertedHealAndHarm
    public boolean isInvertedHealAndHarm() {
        return instance.isUndead();
    }

    // Generated from LivingEntity::getLastDamageSource
    public DamageSourceReference getLastDamageSource() {
        return new DamageSourceReference(instance.getRecentDamageSource());
    }

    // Generated from LivingEntity::getArmorCoverPercentage
    public float getArmorCoverPercentage() {
        return instance.getArmorVisibility();
    }

    // Generated from LivingEntity::wasExperienceConsumed
    public boolean wasExperienceConsumed() {
        return instance.isExperienceDroppingDisabled();
    }

    // Generated from LivingEntity::setAbsorptionAmount
    public void setAbsorptionAmount(float param0) {
        instance.setAbsorptionAmount(param0);
    }

    // Generated from LivingEntity::isSuppressingSlidingDownLadder
    public boolean isSuppressingSlidingDownLadder() {
        return instance.isHoldingOntoLadder();
    }

    // Generated from LivingEntity::skipDropExperience
    public void skipDropExperience() {
        instance.disableExperienceDropping();
    }

    // Generated from LivingEntity::setSleepingPos
    public void setSleepingPos(BlockPosReference param0) {
        instance.setSleepingPosition(param0.instance);
    }

    // Generated from LivingEntity::isCurrentlyGlowing
    public boolean isCurrentlyGlowing() {
        return instance.isGlowing();
    }

    // Generated from LivingEntity::canAttack
    public boolean canAttack(LivingEntityReference<?> param0, TargetingConditionsReference param1) {
        return instance.isTarget(param0.instance, param1.instance);
    }

    // Generated from LivingEntity::canAttack
    public boolean canAttack(LivingEntityReference<?> param0) {
        return instance.canTarget(param0.instance);
    }

    // Generated from LivingEntity::heal
    public void heal(float param0) {
        instance.heal(param0);
    }

    // Generated from LivingEntity::removeAllEffects
    public boolean removeAllEffects() {
        return instance.clearStatusEffects();
    }

    // Generated from LivingEntity::canBeSeenByAnyone
    public boolean canBeSeenByAnyone() {
        return instance.isPartOfGame();
    }

    // Generated from LivingEntity::canBeSeenAsEnemy
    public boolean canBeSeenAsEnemy() {
        return instance.canTakeDamage();
    }

    // Generated from LivingEntity::die
    public void die(DamageSourceReference param0) {
        instance.onDeath(param0.instance);
    }

    // Generated from LivingEntity::getVoicePitch
    public float getVoicePitch() {
        return instance.getSoundPitch();
    }

    // Generated from LivingEntity::isSleeping
    public boolean isSleeping() {
        return instance.isSleeping();
    }

    // Generated from LivingEntity::knockback
    public void knockback(double param0, double param1, double param2) {
        instance.takeKnockback(param0, param1, param2);
    }

    // Generated from LivingEntity::stopSleeping
    public void stopSleeping() {
        instance.wakeUp();
    }

    // Generated from LivingEntity::isBlocking
    public boolean isBlocking() {
        return instance.isBlocking();
    }

    // Generated from LivingEntity::getItemInHand
    public ItemStackReference getItemInHand(Statics.InteractionHands param0) {
        return new ItemStackReference(instance.getStackInHand(param0.instance));
    }

    // Generated from LivingEntity::getKillCredit
    public LivingEntityReference<?> getKillCredit() {
        return new LivingEntityReference<>(instance.getPrimeAdversary());
    }

    // Generated from LivingEntity::getLootTable
    public ResourceLocationReference getLootTable() {
        return new ResourceLocationReference(instance.getLootTable());
    }

    // Generated from LivingEntity::kill
    public void kill() {
        instance.kill();
    }

    // Generated from LivingEntity::getMaxHealth
    public float getMaxHealth() {
        return instance.getMaxHealth();
    }

    // Generated from LivingEntity::setHealth
    public void setHealth(float param0) {
        instance.setHealth(param0);
    }

    // Generated from LivingEntity::hurt
    public boolean hurt(DamageSourceReference param0, float param1) {
        return instance.damage(param0.instance, param1);
    }

    // Generated from LivingEntity::isInWall
    public boolean isInWall() {
        return instance.isInsideWall();
    }

    // Generated from LivingEntity::getSwimAmount
    public float getSwimAmount(float param0) {
        return instance.getLeaningPitch(param0);
    }

    // Generated from LivingEntity::getSleepingPos
    public Optional<BlockPosReference> getSleepingPos() {
        return instance.getSleepingPosition().map(BlockPosReference::new);
    }

    // Generated from LivingEntity::rideableUnderWater
    public boolean dismountsUnderwater() {
        return instance.shouldDismountUnderwater();
    }

    // Generated from LivingEntity::stopRiding
    public void stopRiding() {
        instance.stopRiding();
    }

    // Generated from LivingEntity::isFallFlying
    public boolean isFallFlying() {
        return instance.isFallFlying();
    }

    // Generated from LivingEntity::isDeadOrDying
    public boolean isDeadOrDying() {
        return instance.isDead();
    }

    // Generated from LivingEntity::setLastHurtByMob
    public void setLastHurtByMob(LivingEntityReference<?> param0) {
        instance.setAttacker(param0.instance);
    }

    // Generated from LivingEntity::getItemBySlot
    public ItemStackReference getItemBySlot(Statics.EquipmentSlots param0) {
        return new ItemStackReference(instance.getEquippedStack(param0.instance));
    }

    // Generated from LivingEntity::isBaby
    public boolean isBaby() {
        return instance.isBaby();
    }

    // Generated from LivingEntity::getLastHurtByMob
    public LivingEntityReference<?> getLastHurtByMob() {
        return new LivingEntityReference<>(instance.getAttacker());
    }

    // Generated from LivingEntity::getLastHurtMob
    public LivingEntityReference<?> getLastHurtMob() {
        return new LivingEntityReference<>(instance.getAttacking());
    }

    // Generated from LivingEntity::setLastHurtMob
    public void setLastHurtMob(EntityReference<?> param0) {
        instance.onAttacking(param0.instance);
    }

    // Generated from LivingEntity::getNoActionTime
    public int getNoActionTime() {
        return instance.getDespawnCounter();
    }

    // Generated from LivingEntity::setNoActionTime
    public void setNoActionTime(int param0) {
        instance.setDespawnCounter(param0);
    }

    // Generated from LivingEntity::setDiscardFriction
    public void setDiscardFriction(boolean param0) {
        instance.setNoDrag(param0);
    }

    // Generated from LivingEntity::getHealth
    public float getHealth() {
        return instance.getHealth();
    }

    // Generated from LivingEntity::getViewYRot
    public float getViewYRot(float param0) {
        return instance.getYaw(param0);
    }

    // Generated from LivingEntity::isAutoSpinAttack
    public boolean isAutoSpinAttack() {
        return instance.isUsingRiptide();
    }

    // Generated from LivingEntity::hasLineOfSight
    public boolean hasLineOfSight(EntityReference<?> param0) {
        return instance.canSee(param0.instance);
    }

    // Generated from LivingEntity::animateHurt
    public void animateHurt(float f) {
        instance.animateDamage(f);
    }

    // Generated from LivingEntity::getArmorValue
    public int getArmorValue() {
        return instance.getArmor();
    }

    // Generated from LivingEntity::causeFallDamage
    public boolean causeFallDamage(float param0, float param1, DamageSourceReference param2) {
        return instance.handleFallDamage(param0, param1, param2.instance);
    }

    // Generated from LivingEntity::setOnGround
    public void setOnGround(boolean param0) {
        instance.setOnGround(param0);
    }

    // Generated from LivingEntity::setArrowCount
    public void setArrowCount(int param0) {
        instance.setStuckArrowCount(param0);
    }

    // Generated from LivingEntity::setStingerCount
    public void setStingerCount(int param0) {
        instance.setStingerCount(param0);
    }

    // Generated from LivingEntity::swing
    public void swing(Statics.InteractionHands param0, boolean param1) {
        instance.swingHand(param0.instance, param1);
    }

    // Generated from LivingEntity::swing
    public void swing(Statics.InteractionHands param0) {
        instance.swingHand(param0.instance);
    }

    // Generated from LivingEntity::getArrowCount
    public int getArrowCount() {
        return instance.getStuckArrowCount();
    }

    // Generated from LivingEntity::getStingerCount
    public int getStingerCount() {
        return instance.getStingerCount();
    }

    // Generated from LivingEntity::isHolding
    public boolean isHolding(ItemReference<?> param0) {
        return instance.isHolding(param0.instance);
    }

    public boolean isHolding(LuaFunction function) {
        return instance.isHolding(
                MiscUtil.mapPredicate(
                        MiscUtil.predicateFromFunc(ItemStackReference.class, function),
                        ItemStackReference::new
                )
        );
    }

    // Generated from LivingEntity::getMainHandItem
    public ItemStackReference getMainHandItem() {
        return new ItemStackReference(instance.getMainHandStack());
    }

    // Generated from LivingEntity::setItemSlot
    public void setItemSlot(Statics.EquipmentSlots param0, ItemStackReference param1) {
        instance.equipStack(param0.instance, param1.instance);
    }

    // Generated from LivingEntity::getOffhandItem
    public ItemStackReference getOffhandItem() {
        return new ItemStackReference(instance.getOffHandStack());
    }

    // Generated from LivingEntity::setItemInHand
    public void setItemInHand(Statics.InteractionHands param0, ItemStackReference param1) {
        instance.setStackInHand(param0.instance, param1.instance);
    }

    // Generated from LivingEntity::shouldShowName
    public boolean shouldShowName() {
        return instance.shouldRenderName();
    }

    // Generated from LivingEntity::getJumpBoostPower
    public double getJumpBoostPower() {
        return instance.getJumpBoostVelocityModifier();
    }

    // Generated from LivingEntity::setSprinting
    public void setSprinting(boolean param0) {
        instance.setSprinting(param0);
    }

    // Generated from LivingEntity::hasItemInSlot
    public boolean hasItemInSlot(Statics.EquipmentSlots param0) {
        return instance.hasStackEquipped(param0.instance);
    }

    // Generated from LivingEntity::getSpeed
    public float getSpeed() {
        return instance.getMovementSpeed();
    }

    // Generated from LivingEntity::setSpeed
    public void setSpeed(float param0) {
        instance.setMovementSpeed(param0);
    }

    // Generated from LivingEntity::canFreeze
    public boolean canFreeze() {
        return instance.canFreeze();
    }

    // Generated from LivingEntity::isSensitiveToWater
    public boolean isSensitiveToWater() {
        return instance.hurtByWater();
    }

    // Generated from LivingEntity::getUseItemRemainingTicks
    public int getUseItemRemainingTicks() {
        return instance.getItemUseTimeLeft();
    }

    // Generated from LivingEntity::isAffectedByPotions
    public boolean isAffectedByPotions() {
        return instance.isAffectedBySplashPotions();
    }

    // Generated from LivingEntity::setRecordPlayingNearby
    public void setRecordPlayingNearby(BlockPosReference param0, boolean param1) {
        instance.setNearbySongPlaying(param0.instance, param1);
    }

    // Generated from LivingEntity::setYBodyRot
    public void setYBodyRot(float param0) {
        instance.setBodyYaw(param0);
    }

    // Generated from LivingEntity::getYHeadRot
    public float getYHeadRot() {
        return instance.getHeadYaw();
    }

    // Generated from LivingEntity::getUsedItemHand
    public Statics.InteractionHands getUsedItemHand() {
        return Statics.InteractionHands.get(instance.getActiveHand());
    }

    // Generated from LivingEntity::stopUsingItem
    public void stopUsingItem() {
        instance.clearActiveItem();
    }

    // Generated from LivingEntity::isUsingItem
    public boolean isUsingItem() {
        return instance.isUsingItem();
    }

    // Generated from LivingEntity::isVisuallySwimming
    public boolean isVisuallySwimming() {
        return instance.isInSwimmingPose();
    }

    // Generated from LivingEntity::isPushable
    public boolean isPushable() {
        return instance.isPushable();
    }

    // Generated and modified from LivingEntity::getMainArm
    public boolean isLeftHanded() {
        return instance.getMainArm().equals(Arm.LEFT);
    }

    // Generated from LivingEntity::startUsingItem
    public void startUsingItem(Statics.InteractionHands param0) {
        instance.setCurrentHand(param0.instance);
    }

    // Generated from LivingEntity::setYHeadRot
    public void setYHeadRot(float param0) {
        instance.setHeadYaw(param0);
    }

    // Generated from LivingEntity::getUseItem
    public ItemStackReference getUseItem() {
        return new ItemStackReference(instance.getActiveItem());
    }

    // Generated from LivingEntity::releaseUsingItem
    public void releaseUsingItem() {
        instance.stopUsingItem();
    }

    // Generated from LivingEntity::getFallFlyingTicks
    public int getFallFlyingTicks() {
        return instance.getRoll();
    }

    // Generated from LivingEntity::getTicksUsingItem
    public int getTicksUsingItem() {
        return instance.getItemUseTime();
    }

    // Generated from LivingEntity::canTakeItem
    public boolean canTakeItem(ItemStackReference param0) {
        return instance.canEquip(param0.instance);
    }

    // Generated from LivingEntity::clearSleepingPos
    public void clearSleepingPos() {
        instance.clearSleepingPosition();
    }

    // Generated from LivingEntity::attackable
    public boolean attackable() {
        return instance.isMobOrPlayer();
    }

    // Generated from LivingEntity::eat
    public ItemStackReference eat(LevelReference param0, ItemStackReference param1) {
        return new ItemStackReference(instance.eatFood(param0.instance, param1.instance));
    }

    // Generated from LivingEntity::getBedOrientation
    public Statics.Directions getBedOrientation() {
        return Statics.Directions.get(instance.getSleepingDirection());
    }

    // Generated from LivingEntity::startSleeping
    public void startSleeping(BlockPosReference param0) {
        instance.sleep(param0.instance);
    }

    // Generated from LivingEntity::canDisableShield
    public boolean canDisableShield() {
        return instance.disablesShield();
    }
}