package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@SuppressWarnings("unused")
public interface LivingEntityWrapping {
    static LivingEntity getAsLivingEntity(LivingEntityWrapping wrapping) {
        return (LivingEntity) wrapping;
    }

    @WrapperMethod(alias = "can_breathe_underwater")
    default boolean nucleusWrapper$canBreatheUnderwater() {
        return getAsLivingEntity(this).canBreatheInWater();
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    @WrapperMethod(alias = "is_baby")
    default boolean nucleusWrapper$isBaby() {
        return getAsLivingEntity(this).isBaby();
    }

    @WrapperMethod(alias = "scale")
    default float nucleusWrapper$getScale() {
        return getAsLivingEntity(this).getScaleFactor();
    }

    /**
     * Entity won't drop experience points if this returns false
     */
    @WrapperMethod(alias = "should_drop_experience")
    default boolean nucleusWrapper$shouldDropExperience() {
        return getAsLivingEntity(this).shouldDropXp();
    }

    @WrapperMethod(alias = "experience_reward")
    default int nucleusWrapper$getExperienceReward() {
        return getAsLivingEntity(this).getXpToDrop();
    }

    @Nullable
    @WrapperMethod(alias = "last_hurt_by_mob")
    default LivingEntity nucleusWrapper$getLastHurtByMob() {
        return getAsLivingEntity(this).getAttacker();
    }

    @WrapperMethod(alias = "last_attacker")
    default LivingEntity nucleusWrapper$getLastAttacker() {
        return getAsLivingEntity(this).getLastAttacker();
    }

    @WrapperMethod(alias = "last_hurt_by_mob_timestamp")
    default int nucleusWrapper$getLastHurtByMobTimestamp() {
        return getAsLivingEntity(this).getLastAttackedTime();
    }

    @Nullable
    @WrapperMethod(alias = "last_hurt_mob")
    default LivingEntity nucleusWrapper$getLastHurtMob() {
        return getAsLivingEntity(this).getAttacking();
    }

    @WrapperMethod(alias = "last_hurt_mob_timestamp")
    default int nucleusWrapper$getLastHurtMobTimestamp() {
        return getAsLivingEntity(this).getLastAttackTime();
    }

    @WrapperMethod(alias = "no_action_time")
    default int nucleusWrapper$getNoActionTime() {
        return getAsLivingEntity(this).getDespawnCounter();
    }

    @WrapperMethod(alias = "can_be_seen_as_enemy")
    default boolean nucleusWrapper$canBeSeenAsEnemy() {
        return getAsLivingEntity(this).canTakeDamage();
    }

    @WrapperMethod(alias = "can_be_seen_by_anyone")
    default boolean nucleusWrapper$canBeSeenByAnyone() {
        return getAsLivingEntity(this).isPartOfGame();
    }

    @WrapperMethod(alias = "active_effects") // todo effects
    default Collection<StatusEffectInstance> nucleusWrapper$getActiveEffects() {
        return getAsLivingEntity(this).getStatusEffects();
    }

    /**
     * Returns {@code true} if this entity is undead.
     */
    @WrapperMethod(alias = { "is_inverted_heal_and_harm", "is_effectively_undead" })
    default boolean nucleusWrapper$isInvertedHealAndHarm() {
        return getAsLivingEntity(this).isUndead();
    }

    @WrapperMethod(alias = "health")
    default float nucleusWrapper$getHealth() {
        return getAsLivingEntity(this).getHealth();
    }

    @WrapperMethod(alias = "is_dead_or_dying")
    default boolean nucleusWrapper$isDeadOrDying() {
        return getAsLivingEntity(this).isDead();
    }

    @WrapperMethod(alias = { "hurt_dir", "damage_tilt_yaw" }) // yarn is cool and calls it something sensible: damage_tilt_yaw
    default float nucleusWrapper$getHurtDir() {
        return getAsLivingEntity(this).getDamageTiltYaw();
    }

    @WrapperMethod(alias = "is_alive")
    default boolean nucleusWrapper$isAlive() {
        return getAsLivingEntity(this).isAlive();
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    @WrapperMethod(alias = "armor")
    default int nucleusWrapper$getArmorValue() {
        return getAsLivingEntity(this).getArmor();
    }

    @WrapperMethod(alias = "mainhand_item")
    default ItemStack nucleusWrapper$getMainHandItem() {
        return getAsLivingEntity(this).getMainHandStack();
    }

    @WrapperMethod(alias = "offhand_item")
    default ItemStack nucleusWrapper$getOffhandItem() {
        return getAsLivingEntity(this).getOffHandStack();
    }

    @WrapperMethod(alias = "armor_cover_percentage")
    default float nucleusWrapper$getArmorCoverPercentage() {
        return getAsLivingEntity(this).getArmorVisibility();
    }

    /**
     * Gets the movespeed used for the new AI system.
     */
    @WrapperMethod(alias = "speed")
    default float nucleusWrapper$getSpeed() {
        return getAsLivingEntity(this).getMovementSpeed();
    }

    @WrapperMethod(alias = "is_sensitive_to_water")
    default boolean nucleusWrapper$isSensitiveToWater() {
        return getAsLivingEntity(this).hurtByWater();
    }

    @WrapperMethod(alias = "is_auto_spin_attack")
    default boolean nucleusWrapper$isAutoSpinAttack() {
        return getAsLivingEntity(this).isUsingRiptide();
    }

    @WrapperMethod(alias = "is_pushable")
    default boolean nucleusWrapper$isPushable() {
        return getAsLivingEntity(this).isPushable();
    }

    /**
     * Returns the amount of health added by the Absorption effect.
     */
    @WrapperMethod(alias = "absorption_amount")
    default float nucleusWrapper$getAbsorptionAmount() {
        return getAsLivingEntity(this).getAbsorptionAmount();
    }

    @WrapperMethod(alias = "is_using_item")
    default boolean nucleusWrapper$isUsingItem() {
        return getAsLivingEntity(this).isUsingItem();
    }

    @WrapperMethod(alias = { "use_item", "active_item" })
    default ItemStack nucleusWrapper$getUseItem() {
        return getAsLivingEntity(this).getActiveItem();
    }

    @WrapperMethod(alias = "use_item_remaining_ticks")
    default int nucleusWrapper$getUseItemRemainingTicks() {
        return getAsLivingEntity(this).getItemUseTimeLeft();
    }

    @WrapperMethod(alias = "ticks_using_item")
    default int nucleusWrapper$getTicksUsingItem() {
        return getAsLivingEntity(this).getItemUseTime();
    }

    @WrapperMethod(alias = "is_blocking")
    default boolean nucleusWrapper$isBlocking() {
        return getAsLivingEntity(this).isBlocking();
    }

    @WrapperMethod(alias = "is_fall_flying")
    default boolean nucleusWrapper$isFallFlying() {
        return getAsLivingEntity(this).isFallFlying();
    }

    @WrapperMethod(alias = "fall_flying_ticks")
    default int nucleusWrapper$getFallFlyingTicks() {
        return getAsLivingEntity(this).getRoll();
    }

    /**
     * Returns false if the entity is an armor stand. Returns {@code true} for all other entity living bases.
     */
    @WrapperMethod(alias = "is_affected_by_potions")
    default boolean nucleusWrapper$isAffectedByPotions() {
        return getAsLivingEntity(this).isAffectedBySplashPotions();
    }

    @WrapperMethod(alias = "can_change_dimensions")
    default boolean nucleusWrapper$canChangeDimensions() {
        return getAsLivingEntity(this).canUsePortals();
    }

    @WrapperMethod(alias = "sleeping_pos")
    default BlockPos nucleusWrapper$getSleepingPos() {
        return getAsLivingEntity(this).getSleepingPosition().orElse(null);
    }

    /**
     * Returns whether player is sleeping or not
     */
    @WrapperMethod(alias = "is_sleeping")
    default boolean nucleusWrapper$isSleeping() {
        return getAsLivingEntity(this).isSleeping();
    }

    /**
     * Gets the {@link Direction} for the camera if this entity is sleeping.
     */
    @Nullable
    @WrapperMethod(alias = "bed_orientation")
    default Direction nucleusWrapper$getBedOrientation() {
        return getAsLivingEntity(this).getSleepingDirection();
    }

    @WrapperMethod(alias = "is_in_wall")
    default boolean nucleusWrapper$isInWall() {
        return getAsLivingEntity(this).isInsideWall();
    }

    @WrapperMethod(alias = "can_freeze")
    default boolean nucleusWrapper$canFreeze() {
        return getAsLivingEntity(this).canFreeze();
    }

    @WrapperMethod(alias = { "is_currently_glowing", "is_glowing" })
    default boolean nucleusWrapper$isCurrentlyGlowing() {
        return getAsLivingEntity(this).isGlowing();
    }

    @WrapperMethod(alias = "can_disable_shield")
    default boolean nucleusWrapper$canDisableShield() {
        return getAsLivingEntity(this).disablesShield();
    }

    @WrapperMethod(alias = "max_up_step")
    default float nucleusWrapper$maxUpStep() {
        return getAsLivingEntity(this).getStepHeight();
    }
}
