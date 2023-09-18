package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
        return getAsEntity(this).getTeamColorValue();
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
        return Registries.ENTITY_TYPE.getId(getAsEntity(this).getType()).toString();
    }

    @WrapperMethod(alias = "id")
    default int nucleusWrapper$getId() {
        return getAsEntity(this).getId();
    }

    @WrapperMethod(alias = "tags")
    default Set<String> nucleusWrapper$getTags() {
        return getAsEntity(this).getCommandTags();
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
        return getAsEntity(this).hasPortalCooldown();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    @WrapperMethod(alias = "portal_wait_time")
    default int nucleusWrapper$getPortalWaitTime() {
        return getAsEntity(this).getMaxNetherPortalTime();
    }

    @WrapperMethod(alias = "remaining_fire_ticks")
    default int nucleusWrapper$getRemainingFireTicks() {
        return getAsEntity(this).getFireTicks();
    }

    @WrapperMethod(alias = "on_ground")
    default boolean nucleusWrapper$onGround() {
        return getAsEntity(this).isOnGround();
    }

    @WrapperMethod(alias = "on_pos")
    default BlockPos nucleusWrapper$getOnPos() {
        return getAsEntity(this).getSteppingPos();
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
        return getAsEntity(this).hasNoGravity();
    }

    @WrapperMethod(alias = "dampens_vibrations")
    default boolean nucleusWrapper$dampensVibrations() {
        return getAsEntity(this).occludeVibrationSignals();
    }

    @WrapperMethod(alias = "fire_immune")
    default boolean nucleusWrapper$fireImmune() {
        return getAsEntity(this).isFireImmune();
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning true)
     */
    @WrapperMethod(alias = "is_in_water")
    default boolean nucleusWrapper$isInWater() {
        return getAsEntity(this).isTouchingWater();
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    @WrapperMethod(alias = "is_in_water_or_rain")
    default boolean nucleusWrapper$isInWaterOrRain() {
        return getAsEntity(this).isTouchingWaterOrRain();
    }

    @WrapperMethod(alias = "is_in_water_rain_or_bubble")
    default boolean nucleusWrapper$isInWaterRainOrBubble() {
        return getAsEntity(this).isWet();
    }

    @WrapperMethod(alias = "is_in_water_or_bubble")
    default boolean nucleusWrapper$isInWaterOrBubble() {
        return getAsEntity(this).isInsideWaterOrBubbleColumn();
    }

    @WrapperMethod(alias = "is_under_water")
    default boolean nucleusWrapper$isUnderWater() {
        return getAsEntity(this).isSubmergedInWater();
    }

    @WrapperMethod(alias = "block_state_on")
    default BlockState nucleusWrapper$getBlockStateOn() {
        return getAsEntity(this).getSteppingBlockState();
    }

    @WrapperMethod(alias = "can_spawn_sprint_particle")
    default boolean nucleusWrapper$canSpawnSprintParticle() {
        return getAsEntity(this).shouldSpawnSprintingParticles();
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
        return getAsEntity(this).canHit();
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
        return getAsEntity(this).isInsideWall();
    }

    @WrapperMethod(alias = "can_be_collided_with")
    default boolean nucleusWrapper$canBeCollidedWith() {
        return getAsEntity(this).isCollidable();
    }

    /**
     * Returns the Y Offset of this entity.
     */
    @WrapperMethod(alias = "my_riding_offset")
    default double nucleusWrapper$getMyRidingOffset() {
        return getAsEntity(this).getHeightOffset();
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @WrapperMethod(alias = "passengers_riding_offset")
    default double nucleusWrapper$getPassengersRidingOffset() {
        return getAsEntity(this).getMountedHeightOffset();
    }

    @WrapperMethod(alias = "show_vehicle_health")
    default boolean nucleusWrapper$showVehicleHealth() {
        return getAsEntity(this).isLiving();
    }

    @WrapperMethod(alias = "pick_radius")
    default float nucleusWrapper$getPickRadius() {
        return getAsEntity(this).getTargetingMargin();
    }

    /**
     * Returns a (normalized) vector of where this entity is looking.
     */
    @WrapperMethod(alias = "look_angle")
    default Vec3d nucleusWrapper$getLookAngle() {
        return getAsEntity(this).getRotationVector();
    }

    /**
     * Returns the Entity's pitch and yaw as a {@link Vec2f}.
     */
    @WrapperMethod(alias = "rotation_vector")
    default Vec2f nucleusWrapper$getRotationVector() {
        return getAsEntity(this).getRotationClient();
    }

    @WrapperMethod(alias = "forward")
    default Vec3d nucleusWrapper$getForward() {
        return getAsEntity(this).getRotationVecClient();
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    @WrapperMethod(alias = "dimension_changing_delay")
    default int nucleusWrapper$getDimensionChangingDelay() {
        return getAsEntity(this).getDefaultPortalCooldown();
    }

    @WrapperMethod(alias = "hand_slots")
    default Iterable<ItemStack> nucleusWrapper$getHandSlots() {
        return getAsEntity(this).getHandItems();
    }

    @WrapperMethod(alias = "armor_slots")
    default Iterable<ItemStack> nucleusWrapper$getArmorSlots() {
        return getAsEntity(this).getArmorItems();
    }

    @WrapperMethod(alias = "all_slots")
    default Iterable<ItemStack> nucleusWrapper$getAllSlots() {
        return getAsEntity(this).getItemsEquipped();
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
        return getAsEntity(this).hasVehicle();
    }

    /**
     * If at least 1 entity is riding this one
     */
    @WrapperMethod(alias = "is_vehicle")
    default boolean nucleusWrapper$isVehicle() {
        return getAsEntity(this).hasPassengers();
    }

    @WrapperMethod(alias = "dismounts_underwater")
    default boolean nucleusWrapper$dismountsUnderwater() {
        return getAsEntity(this).shouldDismountUnderwater();
    }

    @WrapperMethod(alias = "is_shift_key_down")
    default boolean nucleusWrapper$isShiftKeyDown() {
        return getAsEntity(this).isSneaking();
    }

    @WrapperMethod(alias = "is_stepping_carefully")
    default boolean nucleusWrapper$isSteppingCarefully() {
        return getAsEntity(this).bypassesSteppingEffects();
    }

    @WrapperMethod(alias = "is_suppressing_bounce")
    default boolean nucleusWrapper$isSuppressingBounce() {
        return getAsEntity(this).bypassesLandingEffects();
    }

    @WrapperMethod(alias = "is_discrete")
    default boolean nucleusWrapper$isDiscrete() {
        return getAsEntity(this).isSneaky();
    }

    @WrapperMethod(alias = "is_descending")
    default boolean nucleusWrapper$isDescending() {
        return getAsEntity(this).isDescending();
    }

    @WrapperMethod(alias = "is_crouching")
    default boolean nucleusWrapper$isCrouching() {
        return getAsEntity(this).isInSneakingPose();
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
        return getAsEntity(this).isInSwimmingPose();
    }

    @WrapperMethod(alias = "is_visually_crawling")
    default boolean nucleusWrapper$isVisuallyCrawling() {
        return getAsEntity(this).isCrawling();
    }

    @WrapperMethod(alias = "is_currently_glowing")
    default boolean nucleusWrapper$isCurrentlyGlowing() {
        return getAsEntity(this).isGlowing();
    }

    @WrapperMethod(alias = "is_invisible")
    default boolean nucleusWrapper$isInvisible() {
        return getAsEntity(this).isInvisible();
    }

    @WrapperMethod(alias = "is_on_rails")
    default boolean nucleusWrapper$isOnRails() {
        return getAsEntity(this).isOnRail();
    }

    @WrapperMethod(alias = "team") //todo teams
    default AbstractTeam nucleusWrapper$getTeam() {
        return getAsEntity(this).getScoreboardTeam();
    }

    @WrapperMethod(alias = "max_air_supply")
    default int nucleusWrapper$getMaxAirSupply() {
        return getAsEntity(this).getMaxAir();
    }

    @WrapperMethod(alias = "air_supply")
    default int nucleusWrapper$getAirSupply() {
        return getAsEntity(this).getAir();
    }

    @WrapperMethod(alias = "ticks_frozen")
    default int nucleusWrapper$getTicksFrozen() {
        return getAsEntity(this).getFrozenTicks();
    }

    @WrapperMethod(alias = "percent_frozen")
    default float nucleusWrapper$getPercentFrozen() {
        return getAsEntity(this).getFreezingScale();
    }

    @WrapperMethod(alias = "is_fully_frozen")
    default boolean nucleusWrapper$isFullyFrozen() {
        return getAsEntity(this).isFrozen();
    }

    @WrapperMethod(alias = "ticks_required_to_freeze")
    default int nucleusWrapper$getTicksRequiredToFreeze() {
        return getAsEntity(this).getMinFreezeDamageTicks();
    }

    @WrapperMethod(alias = "name")
    default Text nucleusWrapper$getName() {
        return getAsEntity(this).getName();
    }

    @WrapperMethod(alias = "y_head_rot")
    default float nucleusWrapper$getYHeadRot() {
        return getAsEntity(this).getHeadYaw();
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
        return getAsEntity(this).canUsePortals();
    }

    /**
     * The maximum height from where the entity is allowed to jump (used in pathfinder)
     */
    @WrapperMethod(alias = "max_fall_distance")
    default int nucleusWrapper$getMaxFallDistance() {
        return getAsEntity(this).getSafeFallDistance();
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    @WrapperMethod(alias = "is_ignoring_block_triggers")
    default boolean nucleusWrapper$isIgnoringBlockTriggers() {
        return getAsEntity(this).canAvoidTraps();
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @WrapperMethod(alias = "display_fire_animation")
    default boolean nucleusWrapper$displayFireAnimation() {
        return getAsEntity(this).doesRenderOnFire();
    }

    @WrapperMethod(alias = "uuid")
    default UUID nucleusWrapper$getUUID() {
        return getAsEntity(this).getUuid();
    }

    @WrapperMethod(alias = "string_uuid")
    default String nucleusWrapper$getStringUUID() {
        return getAsEntity(this).getUuidAsString();
    }

    /**
     * Returns a String to use as this entity's name in the scoreboard/entity selector systems
     */
    @WrapperMethod(alias = "scoreboard_name")
    default String nucleusWrapper$getScoreboardName() {
        return getAsEntity(this).getEntityName();
    }

    @WrapperMethod(alias = "is_pushed_by_fluid")
    default boolean nucleusWrapper$isPushedByFluid() {
        return getAsEntity(this).isPushedByFluids();
    }

    @WrapperMethod(alias = "display_name")
    default Text nucleusWrapper$getDisplayName() {
        return getAsEntity(this).getDisplayName();
    }

    @WrapperMethod(alias = "custom_name")
    default Text nucleusWrapper$getCustomName() {
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
        return getAsEntity(this).shouldRenderName();
    }

    /**
     * Gets the horizontal facing direction of this Entity.
     */
    @WrapperMethod(alias = "direction")
    default Direction nucleusWrapper$getDirection() {
        return getAsEntity(this).getHorizontalFacing();
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
     */
    @WrapperMethod(alias = "motion_direction")
    default Direction nucleusWrapper$getMotionDirection() {
        return getAsEntity(this).getMovementDirection();
    }

    /**
     * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by a minecart, such as a command block).
     */
    @WrapperMethod(alias = "bounding_box_for_culling")
    default Box nucleusWrapper$getBoundingBoxForCulling() {
        return getAsEntity(this).getVisibilityBoundingBox();
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return the overworld
     */
    @WrapperMethod(alias = "command_sender_world")
    default World nucleusWrapper$getCommandSenderWorld() {
        return getAsEntity(this).getEntityWorld();
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
        return getAsEntity(this).isImmuneToExplosion();
    }

    /**
     * Checks if players can use this entity to access operator (permission level 2) commands either directly or indirectly, such as give or setblock. A similar method exists for entities at {@link Entity#entityDataRequiresOperator()}.<p>For example, {@link CommandBlockMinecartEntity#entityDataRequiresOperator() command block minecarts} and {@link SpawnerMinecartEntity#entityDataRequiresOperator() mob spawner minecarts} (spawning command block minecarts or drops) are considered accessible.</p>@return true if this entity offers ways for unauthorized players to use restricted commands
     */
    @WrapperMethod(alias = "only_op_can_set_nbt")
    default boolean nucleusWrapper$onlyOpCanSetNbt() {
        return getAsEntity(this).entityDataRequiresOperator();
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
        return getAsEntity(this).streamSelfAndPassengers();
    }

    @WrapperMethod(alias = "passengers_and_self")
    default Stream<Entity> nucleusWrapper$getPassengersAndSelf() {
        return getAsEntity(this).streamPassengersAndSelf();
    }

    @WrapperMethod(alias = "indirect_passengers")
    default Iterable<Entity> nucleusWrapper$getIndirectPassengers() {
        return getAsEntity(this).getPassengersDeep();
    }

    @WrapperMethod(alias = "has_exactly_one_player_passenger")
    default boolean nucleusWrapper$hasExactlyOnePlayerPassenger() {
        return getAsEntity(this).hasPlayerRider();
    }

    @WrapperMethod(alias = "root_vehicle")
    default Entity nucleusWrapper$getRootVehicle() {
        return getAsEntity(this).getRootVehicle();
    }

    @WrapperMethod(alias = "is_controlled_by_local_instance")
    default boolean nucleusWrapper$isControlledByLocalInstance() {
        return getAsEntity(this).isLogicalSideForUpdatingMovement();
    }

    @WrapperMethod(alias = "is_effective_ai")
    default boolean nucleusWrapper$isEffectiveAi() {
        return getAsEntity(this).canMoveVoluntarily();
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
        return getAsEntity(this).getControllingVehicle();
    }

    @WrapperMethod(alias = "piston_push_reaction")
    default PistonBehavior nucleusWrapper$getPistonPushReaction() {
        return getAsEntity(this).getPistonBehavior();
    }

    @WrapperMethod(alias = "sound_source")
    default SoundCategory nucleusWrapper$getSoundSource() {
        return getAsEntity(this).getSoundCategory();
    }

    @WrapperMethod(alias = "create_command_source_stack")
    default ServerCommandSource nucleusWrapper$createCommandSourceStack() {
        return getAsEntity(this).getCommandSource();
    }

    @WrapperMethod(alias = "accepts_success")
    default boolean nucleusWrapper$acceptsSuccess() {
        return getAsEntity(this).shouldReceiveFeedback();
    }

    @WrapperMethod(alias = "accepts_failure")
    default boolean nucleusWrapper$acceptsFailure() {
        return getAsEntity(this).shouldTrackOutput();
    }

    @WrapperMethod(alias = "should_inform_admins")
    default boolean nucleusWrapper$shouldInformAdmins() {
        return getAsEntity(this).shouldBroadcastConsoleToOps();
    }

    @WrapperMethod(alias = "touching_unloaded_chunk")
    default boolean nucleusWrapper$touchingUnloadedChunk() {
        return getAsEntity(this).isRegionUnloaded();
    }

    @WrapperMethod(alias = "fluid_jump_threshold")
    default double nucleusWrapper$getFluidJumpThreshold() {
        return getAsEntity(this).getSwimHeight();
    }

    @WrapperMethod(alias = "name_tag_offset_y")
    default float nucleusWrapper$getNameTagOffsetY() {
        return getAsEntity(this).getNameLabelHeight();
    }

    @WrapperMethod(alias = "add_entity_packet")
    default Packet<ClientPlayPacketListener> nucleusWrapper$getAddEntityPacket() {
        return getAsEntity(this).createSpawnPacket();
    }

    @WrapperMethod(alias = "position")
    default Vec3d nucleusWrapper$position() {
        return getAsEntity(this).getPos();
    }

    @WrapperMethod(alias = "tracking_position")
    default Vec3d nucleusWrapper$trackingPosition() {
        return getAsEntity(this).getSyncedPos();
    }

    @WrapperMethod(alias = "block_position")
    default BlockPos nucleusWrapper$blockPosition() {
        return getAsEntity(this).getBlockPos();
    }

    @WrapperMethod(alias = "feet_block_state")
    default BlockState nucleusWrapper$getFeetBlockState() {
        return getAsEntity(this).getBlockStateAtPos();
    }

    @WrapperMethod(alias = "chunk_position")
    default ChunkPos nucleusWrapper$chunkPosition() {
        return getAsEntity(this).getChunkPos();
    }

    @WrapperMethod(alias = "delta_movement")
    default Vec3d nucleusWrapper$getDeltaMovement() {
        return getAsEntity(this).getVelocity();
    }

    @WrapperMethod(alias = "random_y")
    default double nucleusWrapper$getRandomY() {
        return getAsEntity(this).getRandomBodyY();
    }

    @WrapperMethod(alias = "eye_y")
    default double nucleusWrapper$getEyeY() {
        return getAsEntity(this).getEyeY();
    }

    @WrapperMethod(alias = "pick_result")
    default ItemStack nucleusWrapper$getPickResult() {
        return getAsEntity(this).getPickBlockStack();
    }

    @WrapperMethod(alias = "can_freeze")
    default boolean nucleusWrapper$canFreeze() {
        return getAsEntity(this).canFreeze();
    }

    @WrapperMethod(alias = "is_freezing")
    default boolean nucleusWrapper$isFreezing() {
        return getAsEntity(this).shouldEscapePowderSnow();
    }

    @WrapperMethod(alias = "yrot")
    default float nucleusWrapper$getYRot() {
        return getAsEntity(this).getYaw();
    }

    @WrapperMethod(alias = "visual_rotation_yin_degrees")
    default float nucleusWrapper$getVisualRotationYInDegrees() {
        return getAsEntity(this).getBodyYaw();
    }

    @WrapperMethod(alias = "xrot")
    default float nucleusWrapper$getXRot() {
        return getAsEntity(this).getPitch();
    }

    @WrapperMethod(alias = "can_sprint")
    default boolean nucleusWrapper$canSprint() {
        return getAsEntity(this).canSprintAsVehicle();
    }

    @WrapperMethod(alias = "max_up_step")
    default float nucleusWrapper$maxUpStep() {
        return getAsEntity(this).getStepHeight();
    }

    @WrapperMethod(alias = "removal_reason")
    default Entity.RemovalReason nucleusWrapper$getRemovalReason() {
        return getAsEntity(this).getRemovalReason();
    }

    @WrapperMethod(alias = "should_be_saved")
    default boolean nucleusWrapper$shouldBeSaved() {
        return getAsEntity(this).shouldSave();
    }

    @WrapperMethod(alias = "is_always_ticking")
    default boolean nucleusWrapper$isAlwaysTicking() {
        return getAsEntity(this).isPlayer();
    }

    @WrapperMethod(alias = "level")
    default World nucleusWrapper$level() {
        return getAsEntity(this).getWorld();
    }

    @WrapperMethod(alias = "damage_sources")
    default DamageSources nucleusWrapper$damageSources() {
        return getAsEntity(this).getDamageSources();
    }
}
