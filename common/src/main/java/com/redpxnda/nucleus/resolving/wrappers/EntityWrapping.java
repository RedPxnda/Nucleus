package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface EntityWrapping {
    static Entity getAsEntity(EntityWrapping wrapping) {
        return (Entity) wrapping;
    }

    @WrapperMethod(alias = "team_color")
    default int nucleusWrapper$getTeamColor() {
        return getAsEntity(this).getTeamColor();
    }

    /**
     * Returns {@code true} if the player is in spectator mode.
     */
    @WrapperMethod(alias = "is_spectator")
    default boolean nucleusWrapper$isSpectator() {
        return getAsEntity(this).isSpectator();
    }

    @WrapperMethod(alias = "type")
    default String nucleusWrapper$getType() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(getAsEntity(this).getType()).toString();
    }

    @WrapperMethod(alias = "id")
    default int nucleusWrapper$getId() {
        return getAsEntity(this).getId();
    }

    @WrapperMethod(alias = "tags")
    default Set<String> nucleusWrapper$getTags() {
        return getAsEntity(this).getTags();
    }

    /*@WrapperMethod(alias = "entity_data")
    default SynchedEntityData nucleusWrapper$getEntityData() {
        return getAsEntity().getEntityData();
    }*/

    /*@WrapperMethod(alias = "pose")
    default Pose nucleusWrapper$getPose() {
        return nucleusWrapper$getSelfAsEntity(this).getPose();
    }*/

    @WrapperMethod(alias = "portal_cooldown")
    default int nucleusWrapper$getPortalCooldown() {
        return getAsEntity(this).getPortalCooldown();
    }

    @WrapperMethod(alias = "is_on_portal_cooldown")
    default boolean nucleusWrapper$isOnPortalCooldown() {
        return getAsEntity(this).isOnPortalCooldown();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    @WrapperMethod(alias = "portal_wait_time")
    default int nucleusWrapper$getPortalWaitTime() {
        return getAsEntity(this).getPortalWaitTime();
    }

    @WrapperMethod(alias = "remaining_fire_ticks")
    default int nucleusWrapper$getRemainingFireTicks() {
        return getAsEntity(this).getRemainingFireTicks();
    }

    @WrapperMethod(alias = "on_ground")
    default boolean nucleusWrapper$onGround() {
        return getAsEntity(this).onGround();
    }

    @WrapperMethod(alias = "on_pos")
    default BlockPos nucleusWrapper$getOnPos() {
        return getAsEntity(this).getOnPos();
    }

    /**
     * @return True if this entity will not play sounds
     */
    @WrapperMethod(alias = "is_silent")
    default boolean nucleusWrapper$isSilent() {
        return getAsEntity(this).isSilent();
    }

    @WrapperMethod(alias = "is_no_gravity")
    default boolean nucleusWrapper$isNoGravity() {
        return getAsEntity(this).isNoGravity();
    }

    @WrapperMethod(alias = "dampens_vibrations")
    default boolean nucleusWrapper$dampensVibrations() {
        return getAsEntity(this).dampensVibrations();
    }

    @WrapperMethod(alias = "fire_immune")
    default boolean nucleusWrapper$fireImmune() {
        return getAsEntity(this).fireImmune();
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning true)
     */
    @WrapperMethod(alias = "is_in_water")
    default boolean nucleusWrapper$isInWater() {
        return getAsEntity(this).isInWater();
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    @WrapperMethod(alias = "is_in_water_or_rain")
    default boolean nucleusWrapper$isInWaterOrRain() {
        return getAsEntity(this).isInWaterOrRain();
    }

    @WrapperMethod(alias = "is_in_water_rain_or_bubble")
    default boolean nucleusWrapper$isInWaterRainOrBubble() {
        return getAsEntity(this).isInWaterRainOrBubble();
    }

    @WrapperMethod(alias = "is_in_water_or_bubble")
    default boolean nucleusWrapper$isInWaterOrBubble() {
        return getAsEntity(this).isInWaterOrBubble();
    }

    @WrapperMethod(alias = "is_under_water")
    default boolean nucleusWrapper$isUnderWater() {
        return getAsEntity(this).isUnderWater();
    }

    @WrapperMethod(alias = "block_state_on")
    default BlockState nucleusWrapper$getBlockStateOn() {
        return getAsEntity(this).getBlockStateOn();
    }

    @WrapperMethod(alias = "can_spawn_sprint_particle")
    default boolean nucleusWrapper$canSpawnSprintParticle() {
        return getAsEntity(this).canSpawnSprintParticle();
    }

    @WrapperMethod(alias = "is_in_lava")
    default boolean nucleusWrapper$isInLava() {
        return getAsEntity(this).isInLava();
    }

    @WrapperMethod(alias = "can_be_hit_by_projectile")
    default boolean nucleusWrapper$canBeHitByProjectile() {
        return getAsEntity(this).canBeHitByProjectile();
    }

    /**
     * Returns {@code true} if other Entities should be prevented from moving through this Entity.
     */
    @WrapperMethod(alias = "is_pickable")
    default boolean nucleusWrapper$isPickable() {
        return getAsEntity(this).isPickable();
    }

    /**
     * Returns {@code true} if this entity should push and be pushed by other entities when colliding.
     */
    @WrapperMethod(alias = "is_pushable")
    default boolean nucleusWrapper$isPushable() {
        return getAsEntity(this).isPushable();
    }

    /**
     * Returns {@code true} if the entity has not been removed.
     */
    @WrapperMethod(alias = "is_alive")
    default boolean nucleusWrapper$isAlive() {
        return getAsEntity(this).isAlive();
    }

    /**
     * Checks if this entity is inside an opaque block.
     */
    @WrapperMethod(alias = "is_in_wall")
    default boolean nucleusWrapper$isInWall() {
        return getAsEntity(this).isInWall();
    }

    @WrapperMethod(alias = "can_be_collided_with")
    default boolean nucleusWrapper$canBeCollidedWith() {
        return getAsEntity(this).canBeCollidedWith();
    }

    /**
     * Returns the Y Offset of this entity.
     */
    @WrapperMethod(alias = "my_riding_offset")
    default double nucleusWrapper$getMyRidingOffset() {
        return getAsEntity(this).getMyRidingOffset();
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @WrapperMethod(alias = "passengers_riding_offset")
    default double nucleusWrapper$getPassengersRidingOffset() {
        return getAsEntity(this).getPassengersRidingOffset();
    }

    @WrapperMethod(alias = "show_vehicle_health")
    default boolean nucleusWrapper$showVehicleHealth() {
        return getAsEntity(this).showVehicleHealth();
    }

    @WrapperMethod(alias = "pick_radius")
    default float nucleusWrapper$getPickRadius() {
        return getAsEntity(this).getPickRadius();
    }

    /**
     * Returns a (normalized) vector of where this entity is looking.
     */
    @WrapperMethod(alias = "look_angle")
    default Vec3 nucleusWrapper$getLookAngle() {
        return getAsEntity(this).getLookAngle();
    }

    /**
     * Returns the Entity's pitch and yaw as a {@link Vec2}.
     */
    @WrapperMethod(alias = "rotation_vector")
    default Vec2 nucleusWrapper$getRotationVector() {
        return getAsEntity(this).getRotationVector();
    }

    @WrapperMethod(alias = "forward")
    default Vec3 nucleusWrapper$getForward() {
        return getAsEntity(this).getForward();
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    @WrapperMethod(alias = "dimension_changing_delay")
    default int nucleusWrapper$getDimensionChangingDelay() {
        return getAsEntity(this).getDimensionChangingDelay();
    }

    @WrapperMethod(alias = "hand_slots")
    default Iterable<ItemStack> nucleusWrapper$getHandSlots() {
        return getAsEntity(this).getHandSlots();
    }

    @WrapperMethod(alias = "armor_slots")
    default Iterable<ItemStack> nucleusWrapper$getArmorSlots() {
        return getAsEntity(this).getArmorSlots();
    }

    @WrapperMethod(alias = "all_slots")
    default Iterable<ItemStack> nucleusWrapper$getAllSlots() {
        return getAsEntity(this).getAllSlots();
    }

    /**
     * Returns {@code true} if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    @WrapperMethod(alias = "is_on_fire")
    default boolean nucleusWrapper$isOnFire() {
        return getAsEntity(this).isOnFire();
    }

    @WrapperMethod(alias = "is_passenger")
    default boolean nucleusWrapper$isPassenger() {
        return getAsEntity(this).isPassenger();
    }

    /**
     * If at least 1 entity is riding this one
     */
    @WrapperMethod(alias = "is_vehicle")
    default boolean nucleusWrapper$isVehicle() {
        return getAsEntity(this).isVehicle();
    }

    @WrapperMethod(alias = "dismounts_underwater")
    default boolean nucleusWrapper$dismountsUnderwater() {
        return getAsEntity(this).dismountsUnderwater();
    }

    @WrapperMethod(alias = "is_shift_key_down")
    default boolean nucleusWrapper$isShiftKeyDown() {
        return getAsEntity(this).isShiftKeyDown();
    }

    @WrapperMethod(alias = "is_stepping_carefully")
    default boolean nucleusWrapper$isSteppingCarefully() {
        return getAsEntity(this).isSteppingCarefully();
    }

    @WrapperMethod(alias = "is_suppressing_bounce")
    default boolean nucleusWrapper$isSuppressingBounce() {
        return getAsEntity(this).isSuppressingBounce();
    }

    @WrapperMethod(alias = "is_discrete")
    default boolean nucleusWrapper$isDiscrete() {
        return getAsEntity(this).isDiscrete();
    }

    @WrapperMethod(alias = "is_descending")
    default boolean nucleusWrapper$isDescending() {
        return getAsEntity(this).isDescending();
    }

    @WrapperMethod(alias = "is_crouching")
    default boolean nucleusWrapper$isCrouching() {
        return getAsEntity(this).isCrouching();
    }

    /**
     * Get if the Entity is sprinting.
     */
    @WrapperMethod(alias = "is_sprinting")
    default boolean nucleusWrapper$isSprinting() {
        return getAsEntity(this).isSprinting();
    }

    @WrapperMethod(alias = "is_swimming")
    default boolean nucleusWrapper$isSwimming() {
        return getAsEntity(this).isSwimming();
    }

    @WrapperMethod(alias = "is_visually_swimming")
    default boolean nucleusWrapper$isVisuallySwimming() {
        return getAsEntity(this).isVisuallySwimming();
    }

    @WrapperMethod(alias = "is_visually_crawling")
    default boolean nucleusWrapper$isVisuallyCrawling() {
        return getAsEntity(this).isVisuallyCrawling();
    }

    @WrapperMethod(alias = "is_currently_glowing")
    default boolean nucleusWrapper$isCurrentlyGlowing() {
        return getAsEntity(this).isCurrentlyGlowing();
    }

    @WrapperMethod(alias = "is_invisible")
    default boolean nucleusWrapper$isInvisible() {
        return getAsEntity(this).isInvisible();
    }

    @WrapperMethod(alias = "is_on_rails")
    default boolean nucleusWrapper$isOnRails() {
        return getAsEntity(this).isOnRails();
    }

    @WrapperMethod(alias = "team") //todo teams
    default Team nucleusWrapper$getTeam() {
        return getAsEntity(this).getTeam();
    }

    @WrapperMethod(alias = "max_air_supply")
    default int nucleusWrapper$getMaxAirSupply() {
        return getAsEntity(this).getMaxAirSupply();
    }

    @WrapperMethod(alias = "air_supply")
    default int nucleusWrapper$getAirSupply() {
        return getAsEntity(this).getAirSupply();
    }

    @WrapperMethod(alias = "ticks_frozen")
    default int nucleusWrapper$getTicksFrozen() {
        return getAsEntity(this).getTicksFrozen();
    }

    @WrapperMethod(alias = "percent_frozen")
    default float nucleusWrapper$getPercentFrozen() {
        return getAsEntity(this).getPercentFrozen();
    }

    @WrapperMethod(alias = "is_fully_frozen")
    default boolean nucleusWrapper$isFullyFrozen() {
        return getAsEntity(this).isFullyFrozen();
    }

    @WrapperMethod(alias = "ticks_required_to_freeze")
    default int nucleusWrapper$getTicksRequiredToFreeze() {
        return getAsEntity(this).getTicksRequiredToFreeze();
    }

    @WrapperMethod(alias = "name")
    default Component nucleusWrapper$getName() {
        return getAsEntity(this).getName();
    }

    @WrapperMethod(alias = "y_head_rot")
    default float nucleusWrapper$getYHeadRot() {
        return getAsEntity(this).getYHeadRot();
    }

    /**
     * Returns {@code true} if it's possible to attack this entity with an item.
     */
    @WrapperMethod(alias = "is_attackable")
    default boolean nucleusWrapper$isAttackable() {
        return getAsEntity(this).isAttackable();
    }

    @WrapperMethod(alias = "is_invulnerable")
    default boolean nucleusWrapper$isInvulnerable() {
        return getAsEntity(this).isInvulnerable();
    }

    /**
     * Returns false if this Entity can't move between dimensions. True if it can.
     */
    @WrapperMethod(alias = "can_change_dimensions")
    default boolean nucleusWrapper$canChangeDimensions() {
        return getAsEntity(this).canChangeDimensions();
    }

    /**
     * The maximum height from where the entity is allowed to jump (used in pathfinder)
     */
    @WrapperMethod(alias = "max_fall_distance")
    default int nucleusWrapper$getMaxFallDistance() {
        return getAsEntity(this).getMaxFallDistance();
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    @WrapperMethod(alias = "is_ignoring_block_triggers")
    default boolean nucleusWrapper$isIgnoringBlockTriggers() {
        return getAsEntity(this).isIgnoringBlockTriggers();
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @WrapperMethod(alias = "display_fire_animation")
    default boolean nucleusWrapper$displayFireAnimation() {
        return getAsEntity(this).displayFireAnimation();
    }

    @WrapperMethod(alias = "uuid")
    default UUID nucleusWrapper$getUUID() {
        return getAsEntity(this).getUUID();
    }

    @WrapperMethod(alias = "string_uuid")
    default String nucleusWrapper$getStringUUID() {
        return getAsEntity(this).getStringUUID();
    }

    /**
     * Returns a String to use as this entity's name in the scoreboard/entity selector systems
     */
    @WrapperMethod(alias = "scoreboard_name")
    default String nucleusWrapper$getScoreboardName() {
        return getAsEntity(this).getScoreboardName();
    }

    @WrapperMethod(alias = "is_pushed_by_fluid")
    default boolean nucleusWrapper$isPushedByFluid() {
        return getAsEntity(this).isPushedByFluid();
    }

    @WrapperMethod(alias = "display_name")
    default Component nucleusWrapper$getDisplayName() {
        return getAsEntity(this).getDisplayName();
    }

    @WrapperMethod(alias = "custom_name")
    default Component nucleusWrapper$getCustomName() {
        return getAsEntity(this).getCustomName();
    }

    @WrapperMethod(alias = "has_custom_name")
    default boolean nucleusWrapper$hasCustomName() {
        return getAsEntity(this).hasCustomName();
    }

    @WrapperMethod(alias = "is_custom_name_visible")
    default boolean nucleusWrapper$isCustomNameVisible() {
        return getAsEntity(this).isCustomNameVisible();
    }

    @WrapperMethod(alias = "should_show_name")
    default boolean nucleusWrapper$shouldShowName() {
        return getAsEntity(this).shouldShowName();
    }

    /**
     * Gets the horizontal facing direction of this Entity.
     */
    @WrapperMethod(alias = "direction")
    default Direction nucleusWrapper$getDirection() {
        return getAsEntity(this).getDirection();
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
     */
    @WrapperMethod(alias = "motion_direction")
    default Direction nucleusWrapper$getMotionDirection() {
        return getAsEntity(this).getMotionDirection();
    }

    /**
     * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by a minecart, such as a command block).
     */
    @WrapperMethod(alias = "bounding_box_for_culling")
    default AABB nucleusWrapper$getBoundingBoxForCulling() {
        return getAsEntity(this).getBoundingBoxForCulling();
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return the overworld
     */
    @WrapperMethod(alias = "command_sender_world")
    default Level nucleusWrapper$getCommandSenderWorld() {
        return getAsEntity(this).getCommandSenderWorld();
    }

    /**
     * Get the Minecraft server instance
     */
    @WrapperMethod(alias = "server")
    default MinecraftServer nucleusWrapper$getServer() {
        return getAsEntity(this).getServer();
    }

    @WrapperMethod(alias = "ignore_explosion")
    default boolean nucleusWrapper$ignoreExplosion() {
        return getAsEntity(this).ignoreExplosion();
    }

    /**
     * Checks if players can use this entity to access operator (permission level 2) commands either directly or indirectly, such as give or setblock. A similar method exists for entities at {@link Entity#onlyOpCanSetNbt()}.<p>For example, {@link MinecartCommandBlock#onlyOpCanSetNbt() command block minecarts} and {@link MinecartSpawner#onlyOpCanSetNbt() mob spawner minecarts} (spawning command block minecarts or drops) are considered accessible.</p>@return true if this entity offers ways for unauthorized players to use restricted commands
     */
    @WrapperMethod(alias = "only_op_can_set_nbt")
    default boolean nucleusWrapper$onlyOpCanSetNbt() {
        return getAsEntity(this).onlyOpCanSetNbt();
    }

    @WrapperMethod(alias = "controlling_passenger")
    default LivingEntity nucleusWrapper$getControllingPassenger() {
        return getAsEntity(this).getControllingPassenger();
    }

    @WrapperMethod(alias = "first_passenger")
    default Entity nucleusWrapper$getFirstPassenger() {
        return getAsEntity(this).getFirstPassenger();
    }

    @WrapperMethod(alias = "self_and_passengers")
    default Stream<Entity> nucleusWrapper$getSelfAndPassengers() {
        return getAsEntity(this).getSelfAndPassengers();
    }

    @WrapperMethod(alias = "passengers_and_self")
    default Stream<Entity> nucleusWrapper$getPassengersAndSelf() {
        return getAsEntity(this).getPassengersAndSelf();
    }

    @WrapperMethod(alias = "indirect_passengers")
    default Iterable<Entity> nucleusWrapper$getIndirectPassengers() {
        return getAsEntity(this).getIndirectPassengers();
    }

    @WrapperMethod(alias = "has_exactly_one_player_passenger")
    default boolean nucleusWrapper$hasExactlyOnePlayerPassenger() {
        return getAsEntity(this).hasExactlyOnePlayerPassenger();
    }

    @WrapperMethod(alias = "root_vehicle")
    default Entity nucleusWrapper$getRootVehicle() {
        return getAsEntity(this).getRootVehicle();
    }

    @WrapperMethod(alias = "is_controlled_by_local_instance")
    default boolean nucleusWrapper$isControlledByLocalInstance() {
        return getAsEntity(this).isControlledByLocalInstance();
    }

    @WrapperMethod(alias = "is_effective_ai")
    default boolean nucleusWrapper$isEffectiveAi() {
        return getAsEntity(this).isEffectiveAi();
    }

    /**
     * Get entity this is riding
     */
    @WrapperMethod(alias = "vehicle")
    default Entity nucleusWrapper$getVehicle() {
        return getAsEntity(this).getVehicle();
    }

    @WrapperMethod(alias = "controlled_vehicle")
    default Entity nucleusWrapper$getControlledVehicle() {
        return getAsEntity(this).getControlledVehicle();
    }

    @WrapperMethod(alias = "piston_push_reaction")
    default PushReaction nucleusWrapper$getPistonPushReaction() {
        return getAsEntity(this).getPistonPushReaction();
    }

    @WrapperMethod(alias = "sound_source")
    default SoundSource nucleusWrapper$getSoundSource() {
        return getAsEntity(this).getSoundSource();
    }

    @WrapperMethod(alias = "create_command_source_stack")
    default CommandSourceStack nucleusWrapper$createCommandSourceStack() {
        return getAsEntity(this).createCommandSourceStack();
    }

    @WrapperMethod(alias = "accepts_success")
    default boolean nucleusWrapper$acceptsSuccess() {
        return getAsEntity(this).acceptsSuccess();
    }

    @WrapperMethod(alias = "accepts_failure")
    default boolean nucleusWrapper$acceptsFailure() {
        return getAsEntity(this).acceptsFailure();
    }

    @WrapperMethod(alias = "should_inform_admins")
    default boolean nucleusWrapper$shouldInformAdmins() {
        return getAsEntity(this).shouldInformAdmins();
    }

    @WrapperMethod(alias = "touching_unloaded_chunk")
    default boolean nucleusWrapper$touchingUnloadedChunk() {
        return getAsEntity(this).touchingUnloadedChunk();
    }

    @WrapperMethod(alias = "fluid_jump_threshold")
    default double nucleusWrapper$getFluidJumpThreshold() {
        return getAsEntity(this).getFluidJumpThreshold();
    }

    @WrapperMethod(alias = "name_tag_offset_y")
    default float nucleusWrapper$getNameTagOffsetY() {
        return getAsEntity(this).getNameTagOffsetY();
    }

    @WrapperMethod(alias = "add_entity_packet")
    default Packet<ClientGamePacketListener> nucleusWrapper$getAddEntityPacket() {
        return getAsEntity(this).getAddEntityPacket();
    }

    @WrapperMethod(alias = "position")
    default Vec3 nucleusWrapper$position() {
        return getAsEntity(this).position();
    }

    @WrapperMethod(alias = "tracking_position")
    default Vec3 nucleusWrapper$trackingPosition() {
        return getAsEntity(this).trackingPosition();
    }

    @WrapperMethod(alias = "block_position")
    default BlockPos nucleusWrapper$blockPosition() {
        return getAsEntity(this).blockPosition();
    }

    @WrapperMethod(alias = "feet_block_state")
    default BlockState nucleusWrapper$getFeetBlockState() {
        return getAsEntity(this).getFeetBlockState();
    }

    @WrapperMethod(alias = "chunk_position")
    default ChunkPos nucleusWrapper$chunkPosition() {
        return getAsEntity(this).chunkPosition();
    }

    @WrapperMethod(alias = "delta_movement")
    default Vec3 nucleusWrapper$getDeltaMovement() {
        return getAsEntity(this).getDeltaMovement();
    }

    @WrapperMethod(alias = "random_y")
    default double nucleusWrapper$getRandomY() {
        return getAsEntity(this).getRandomY();
    }

    @WrapperMethod(alias = "eye_y")
    default double nucleusWrapper$getEyeY() {
        return getAsEntity(this).getEyeY();
    }

    @WrapperMethod(alias = "pick_result")
    default ItemStack nucleusWrapper$getPickResult() {
        return getAsEntity(this).getPickResult();
    }

    @WrapperMethod(alias = "can_freeze")
    default boolean nucleusWrapper$canFreeze() {
        return getAsEntity(this).canFreeze();
    }

    @WrapperMethod(alias = "is_freezing")
    default boolean nucleusWrapper$isFreezing() {
        return getAsEntity(this).isFreezing();
    }

    @WrapperMethod(alias = "yrot")
    default float nucleusWrapper$getYRot() {
        return getAsEntity(this).getYRot();
    }

    @WrapperMethod(alias = "visual_rotation_yin_degrees")
    default float nucleusWrapper$getVisualRotationYInDegrees() {
        return getAsEntity(this).getVisualRotationYInDegrees();
    }

    @WrapperMethod(alias = "xrot")
    default float nucleusWrapper$getXRot() {
        return getAsEntity(this).getXRot();
    }

    @WrapperMethod(alias = "can_sprint")
    default boolean nucleusWrapper$canSprint() {
        return getAsEntity(this).canSprint();
    }

    @WrapperMethod(alias = "max_up_step")
    default float nucleusWrapper$maxUpStep() {
        return getAsEntity(this).maxUpStep();
    }

    @WrapperMethod(alias = "removal_reason")
    default Entity.RemovalReason nucleusWrapper$getRemovalReason() {
        return getAsEntity(this).getRemovalReason();
    }

    @WrapperMethod(alias = "should_be_saved")
    default boolean nucleusWrapper$shouldBeSaved() {
        return getAsEntity(this).shouldBeSaved();
    }

    @WrapperMethod(alias = "is_always_ticking")
    default boolean nucleusWrapper$isAlwaysTicking() {
        return getAsEntity(this).isAlwaysTicking();
    }

    @WrapperMethod(alias = "level")
    default Level nucleusWrapper$level() {
        return getAsEntity(this).level();
    }

    @WrapperMethod(alias = "damage_sources")
    default DamageSources nucleusWrapper$damageSources() {
        return getAsEntity(this).damageSources();
    }
}
