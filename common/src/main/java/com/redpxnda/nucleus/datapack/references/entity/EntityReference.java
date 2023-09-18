package com.redpxnda.nucleus.datapack.references.entity;

import com.redpxnda.nucleus.capability.entity.EntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import com.redpxnda.nucleus.capability.entity.EntityDataRegistry;
import com.redpxnda.nucleus.datapack.references.LevelReference;
import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.Statics;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.datapack.references.storage.*;
import com.redpxnda.nucleus.util.StatManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class EntityReference<E extends Entity> extends Reference<E> {
    static { Reference.register(EntityReference.class); }

    public EntityReference(E instance) {
        super(instance);
    }

    public Map<String, Object> getStats() {
        return StatManager.entity.evaluate(instance);
    }

    public <T extends EntityCapability<?>> T getCapability(Class<T> cap) {
        return EntityDataManager.getCapability(instance, cap);
    }

    public EntityCapability<?> getCapability(String str) {
        return EntityDataManager.getCapability(instance, EntityDataRegistry.getFromId(new Identifier(str)));
    }
    public EntityCapability<?> getCapability(ResourceLocationReference ref) {
        return EntityDataManager.getCapability(instance, EntityDataRegistry.getFromId(ref.instance));
    }

    public boolean is(String str) {
        return instance.getType().equals(Registries.ENTITY_TYPE.get(new Identifier(str)));
    }
    public boolean is(ResourceLocationReference ref) {
        return instance.getType().equals(Registries.ENTITY_TYPE.get(ref.instance));
    }

    // Generated from Entity::getName
    public ComponentReference<?> getName() {
        return new ComponentReference<>(instance.getName());
    }

    // Generated from Entity::remove
    public void remove() {
        instance.remove(Entity.RemovalReason.DISCARDED);
    }

    // Generated from Entity::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from Entity::position
    public Vec3Reference position() {
        return new Vec3Reference(instance.getPos());
    }

    // Generated from Entity::isAlive
    public boolean isAlive() {
        return instance.isAlive();
    }

    // Generated from Entity::getId
    public int getId() {
        return instance.getId();
    }

    // Generated from Entity::getType
    public String getType() {
        return Registries.ENTITY_TYPE.getId(instance.getType()).toString();
    }

    // Generated from Entity::getSlot
    public SlotAccessReference getSlot(int param0) {
        return new SlotAccessReference(instance.getStackReference(param0));
    }

    // Generated from Entity::is
    public boolean is(EntityReference<?> param0) {
        return instance.isPartOf(param0.instance);
    }

    // Generated from Entity::push
    public void push(double param0, double param1, double param2) {
        instance.addVelocity(param0, param1, param2);
    }

    // Generated from Entity::getDisplayName
    public ComponentReference<?> getDisplayName() {
        return new ComponentReference<>(instance.getDisplayName());
    }

    // Generated from Entity::getLevel
    public LevelReference getLevel() {
        return new LevelReference(instance.getWorld());
    }

    // Generated from Entity::setRemainingFireTicks
    public void setRemainingFireTicks(int param0) {
        instance.setFireTicks(param0);
    }

    // Generated from Entity::canSpawnSprintParticle
    public boolean canSpawnSprintParticle() {
        return instance.shouldSpawnSprintingParticles();
    }

    // Generated from Entity::getDimensionChangingDelay
    public int getDimensionChangingDelay() {
        return instance.getDefaultPortalCooldown();
    }

    // Generated from Entity::getRemainingFireTicks
    public int getRemainingFireTicks() {
        return instance.getFireTicks();
    }

    // Generated from Entity::isPassengerOfSameVehicle
    public boolean isPassengerOfSameVehicle(EntityReference<?> param0) {
        return instance.isConnectedThroughVehicle(param0.instance);
    }

    public void sendSystemMessage(ComponentReference<?> component) {
        instance.sendMessage(component.instance);
    }

    // Generated from Entity::isInWaterRainOrBubble
    public boolean isInWaterRainOrBubble() {
        return instance.isWet();
    }

    // Generated from Entity::getControllingPassenger
    public EntityReference<?> getControllingPassenger() {
        return new EntityReference<>(instance.getControllingPassenger());
    }

    // Generated from Entity::canChangeDimensions
    public boolean canChangeDimensions() {
        return instance.canUsePortals();
    }

    // Generated from Entity::setCustomNameVisible
    public void setCustomNameVisible(boolean param0) {
        instance.setCustomNameVisible(param0);
    }

    // Generated from Entity::isCustomNameVisible
    public boolean isCustomNameVisible() {
        return instance.isCustomNameVisible();
    }

    // Generated from Entity::getTicksRequiredToFreeze
    public int getTicksRequiredToFreeze() {
        return instance.getMinFreezeDamageTicks();
    }

    // Generated from Entity::touchingUnloadedChunk
    public boolean touchingUnloadedChunk() {
        return instance.isRegionUnloaded();
    }

    // Generated from Entity::hasControllingPassenger
    public boolean hasControllingPassenger() {
        return instance.hasControllingPassenger();
    }

    // Generated from Entity::hasIndirectPassenger
    public boolean hasIndirectPassenger(EntityReference<?> param0) {
        return instance.hasPassengerDeep(param0.instance);
    }

    // Generated from Entity::hasExactlyOnePlayerPassenger
    public boolean hasExactlyOnePlayerPassenger() {
        return instance.hasPlayerRider();
    }

    // Generated from Entity::getIndirectPassengers
    public List<EntityReference<Entity>> getSelfAndPassengers() {
        return instance.streamSelfAndPassengers().map(EntityReference::new).toList();
    }

    // Generated from Entity::setOnGround
    public void setOnGround(boolean param0) {
        instance.setOnGround(param0);
    }

    // Generated from Entity::isOnFire
    public boolean isOnFire() {
        return instance.isOnFire();
    }

    // Generated from Entity::getDeltaMovement
    public Vec3Reference getDeltaMovement() {
        return new Vec3Reference(instance.getVelocity());
    }

    // Generated from Entity::setDeltaMovement
    public void setDeltaMovement(double param0, double param1, double param2) {
        instance.setVelocity(param0, param1, param2);
    }

    // Generated from Entity::isOnGround
    public boolean onGround() {
        return instance.isOnGround();
    }

    // Generated from Entity::resetFallDistance
    public void resetFallDistance() {
        instance.onLanding();
    }

    // Generated from Entity::hurt
    public boolean hurt(DamageSourceReference param0, float param1) {
        return instance.damage(param0.instance, param1);
    }

    // Generated from Entity::setSecondsOnFire
    public void setSecondsOnFire(int param0) {
        instance.setOnFireFor(param0);
    }

    // Generated from Entity::setPortalCooldown
    public void resetPortalCooldown() {
        instance.resetPortalCooldown();
    }

    // Generated from Entity::isInLava
    public boolean isInLava() {
        return instance.isInLava();
    }

    // Generated from Entity::lavaHurt
    public void burnWithLava() {
        instance.setOnFireFromLava();
    }

    // Generated from Entity::getTicksFrozen
    public int getTicksFrozen() {
        return instance.getFrozenTicks();
    }

    // Generated from Entity::setTicksFrozen
    public void setTicksFrozen(int param0) {
        instance.setFrozenTicks(param0);
    }

    // Generated from Entity::isOnPortalCooldown
    public boolean isOnPortalCooldown() {
        return instance.hasPortalCooldown();
    }

    // Generated from Entity::isInWater
    public boolean isInWater() {
        return instance.isTouchingWater();
    }

    // Generated from Entity::getBlockStateOn
    public BlockStateReference getBlockStateOn() {
        return new BlockStateReference(instance.getSteppingBlockState());
    }

    // Generated from Entity::getOnPos
    public BlockPosReference getOnPos() {
        return new BlockPosReference(instance.getSteppingPos());
    }

    // Generated from Entity::isSilent
    public boolean isSilent() {
        return instance.isSilent();
    }

    // Generated from Entity::isNoGravity
    public boolean isNoGravity() {
        return instance.hasNoGravity();
    }

    // Generated from Entity::setNoGravity
    public void setNoGravity(boolean param0) {
        instance.setNoGravity(param0);
    }

    // Generated from Entity::setSilent
    public void setSilent(boolean param0) {
        instance.setSilent(param0);
    }

    // Generated from Entity::isSprinting
    public boolean isSprinting() {
        return instance.isSprinting();
    }

    // Generated from Entity::isInWaterOrBubble
    public boolean isInWaterOrBubble() {
        return instance.isInsideWaterOrBubbleColumn();
    }

    // Generated from Entity::isSwimming
    public boolean isSwimming() {
        return instance.isSwimming();
    }

    // Generated from Entity::isInWaterOrRain
    public boolean isInWaterOrRain() {
        return instance.isTouchingWaterOrRain();
    }

    // Generated from Entity::isUnderWater
    public boolean isUnderWater() {
        return instance.isSubmergedInWater();
    }

    // Generated from Entity::getEyeY
    public double getEyeY() {
        return instance.getEyeY();
    }

    // Generated from Entity::getPassengers
    public List<EntityReference<Entity>> getPassengers() {
        return instance.getPassengerList().stream().map(EntityReference::new).toList();
    }

    // Generated from Entity::causeFallDamage
    public boolean causeFallDamage(float param0, float param1, DamageSourceReference param2) {
        return instance.handleFallDamage(param0, param1, param2.instance);
    }

    // Generated from Entity::isCrouching
    public boolean isCrouching() {
        return instance.isInSneakingPose();
    }

    // Generated from Entity::moveTo
    public void moveTo(double param0, double param1, double param2, float param3, float param4) {
        instance.refreshPositionAndAngles(param0, param1, param2, param3, param4);
    }

    // Generated from Entity::moveTo
    public void moveTo(Vec3Reference param0) {
        instance.refreshPositionAfterTeleport(param0.instance);
    }

    // Generated from Entity::moveTo
    public void moveTo(double param0, double param1, double param2) {
        instance.refreshPositionAfterTeleport(param0, param1, param2);
    }

    // Generated from Entity::moveRelative
    public void moveRelative(float param0, Vec3Reference param1) {
        instance.updateVelocity(param0, param1.instance);
    }

    // Generated from Entity::distanceTo
    public float distanceTo(EntityReference<?> param0) {
        return instance.distanceTo(param0.instance);
    }

    // Generated from Entity::getBlockZ
    public int getBlockZ() {
        return instance.getBlockZ();
    }

    // Generated from Entity::getBlockX
    public int getBlockX() {
        return instance.getBlockX();
    }

    // Generated from Entity::playerTouch
    public void playerTouch(PlayerEntity param0) {
        instance.onPlayerCollision(param0);
    }

    // Generated from Entity::isPushable
    public boolean isPushable() {
        return instance.isPushable();
    }

    // Generated from Entity::getEyePosition
    public Vec3Reference getEyePosition() {
        return new Vec3Reference(instance.getEyePos());
    }

    // Generated from Entity::isInvulnerableTo
    public boolean isInvulnerableTo(DamageSourceReference param0) {
        return instance.isInvulnerableTo(param0.instance);
    }

    // Generated from Entity::getAirSupply
    public int getAirSupply() {
        return instance.getAir();
    }

    // Generated from Entity::getCustomName
    public ComponentReference<?> getCustomName() {
        return new ComponentReference<>(instance.getCustomName());
    }

    // Generated from Entity::getUUID
    public UUID getUUID() {
        return instance.getUuid();
    }

    // Generated from Entity::chunkPosition
    public ChunkPosReference chunkPosition() {
        return new ChunkPosReference(instance.getChunkPos());
    }

    // Generated from Entity::setAirSupply
    public void setAirSupply(int param0) {
        instance.setAir(param0);
    }

    // Generated from Entity::setCustomName
    public void setCustomName(ComponentReference<?> param0) {
        instance.setCustomName(param0.instance);
    }

    // Generated from Entity::setYHeadRot
    public void setYHeadRot(float param0) {
        instance.setHeadYaw(param0);
    }

    // Generated from Entity::setYBodyRot
    public void setYBodyRot(float param0) {
        instance.setBodyYaw(param0);
    }

    // Generated from Entity::hasPassenger
    public boolean hasPassenger(EntityReference<?> param0) {
        return instance.hasPassenger(param0.instance);
    }

    // Generated from Entity::spawnAtLocation
    public EntityReference<ItemEntity> spawnAtLocation(ItemStackReference param0) {
        return new EntityReference<>(instance.dropStack(param0.instance));
    }

    // Generated from Entity::isInWall
    public boolean isInWall() {
        return instance.isInsideWall();
    }

    // Generated from Entity::interact
    public void interact(PlayerReference param0, Statics.InteractionHands param1) {
        instance.interact(param0.instance, param1.instance);
    }

    // Generated from Entity::removeVehicle
    public void removeVehicle() {
        instance.dismountVehicle();
    }

    // Generated from Entity::isShiftKeyDown
    public boolean isShiftKeyDown() {
        return instance.isSneaking();
    }

    // Generated from Entity::getLookAngle
    public Vec3Reference getLookAngle() {
        return new Vec3Reference(instance.getRotationVector());
    }

    // Generated from Entity::startRiding
    public boolean startRiding(EntityReference<?> param0) {
        return instance.startRiding(param0.instance, true);
    }

    // Generated from Entity::getRotationVector
    public Vec2Reference getRotationVector() {
        return new Vec2Reference(instance.getRotationClient());
    }

//    // Generated from Entity::getServer
//    //TODO:
//    public MinecraftServer getServer() {
//        return instance.getServer();
//    }

    // Generated from Entity::getForward
    public Vec3Reference getForward() {
        return new Vec3Reference(instance.getRotationVecClient());
    }

    // Generated from Entity::animateHurt
    public void animateHurt(float f) {
        instance.animateDamage(f);
    }

    // Generated from Entity::getAllSlots
    public List<ItemStackReference> getAllSlots() {
        return StreamSupport.stream(instance.getItemsEquipped().spliterator(), false)
                .map(ItemStackReference::new)
                .toList();
    }

    // Generated from Entity::isVisuallyCrawling
    public boolean isVisuallyCrawling() {
        return instance.isCrawling();
    }

    // Generated from Entity::getScoreboardName
    public String getScoreboardName() {
        return instance.getEntityName();
    }

    // Generated from Entity::getHandSlots
    public Iterable<ItemStackReference> getHandSlots() {
        return StreamSupport.stream(instance.getHandItems().spliterator(), false)
                .map(ItemStackReference::new)
                .toList();
    }

    // Generated from Entity::isCurrentlyGlowing
    public boolean isCurrentlyGlowing() {
        return instance.isGlowing();
    }

    // Generated from Entity::getArmorSlots
    public Iterable<ItemStackReference> getArmorSlots() {
        return StreamSupport.stream(instance.getArmorItems().spliterator(), false)
                .map(ItemStackReference::new)
                .toList();
    }

    // Generated from Entity::setItemSlot
    public void setItemSlot(Statics.EquipmentSlots param0, ItemStackReference param1) {
        instance.equipStack(param0.instance, param1.instance);
    }

    // Generated from Entity::isInvisibleTo
    public boolean isInvisibleTo(PlayerReference param0) {
        return instance.isInvisibleTo(param0.instance);
    }

    // Generated from Entity::setShiftKeyDown
    public void setShiftKeyDown(boolean param0) {
        instance.setSneaking(param0);
    }

    // Generated from Entity::setSprinting
    public void setSprinting(boolean param0) {
        instance.setSprinting(param0);
    }

    // Generated from Entity::isVisuallySwimming
    public boolean isVisuallySwimming() {
        return instance.isInSwimmingPose();
    }

    // Generated from Entity::isInvisible
    public boolean isInvisible() {
        return instance.isInvisible();
    }

    // Generated from Entity::setInvisible
    public void setInvisible(boolean param0) {
        instance.setInvisible(param0);
    }

    // Generated from Entity::isAlliedTo
    public boolean isAlliedTo(EntityReference<?> param0) {
        return instance.isTeammate(param0.instance);
    }

    // Generated from Entity::getPercentFrozen
    public float getPercentFrozen() {
        return instance.getFreezingScale();
    }

    // Generated from Entity::isFullyFrozen
    public boolean isFullyFrozen() {
        return instance.isFrozen();
    }

    // Generated from Entity::setInvulnerable
    public void setInvulnerable(boolean param0) {
        instance.setInvulnerable(param0);
    }

    // Generated from Entity::isAttackable
    public boolean isAttackable() {
        return instance.isAttackable();
    }

    // Generated from Entity::isInvulnerable
    public boolean isInvulnerable() {
        return instance.isInvulnerable();
    }

    // Generated from Entity::getYHeadRot
    public float getYHeadRot() {
        return instance.getHeadYaw();
    }

    // Generated from Entity::getEyeHeight
    public float getEyeHeight() {
        return instance.getStandingEyeHeight();
    }

    // Generated from Entity::isSpectator
    public boolean isSpectator() {
        return instance.isSpectator();
    }

    // Generated from Entity::setPos
    public void setPos(Vec3Reference param0) {
        instance.setPosition(param0.instance);
    }

    // Generated from Entity::setPos
    public void setPos(double param0, double param1, double param2) {
        instance.setPosition(param0, param1, param2);
    }

    // Generated from Entity::unRide
    public void unRide() {
        instance.detach();
    }

    // Generated from Entity::getTeamColor
    public int getTeamColor() {
        return instance.getTeamColorValue();
    }

    // Generated from Entity::isVehicle
    public boolean isVehicle() {
        return instance.hasPassengers();
    }

    // Generated from Entity::getZ
    public double getZ() {
        return instance.getZ();
    }

    // Generated from Entity::getY
    public double getY() {
        return instance.getY();
    }

    // Generated from Entity::getBoundingBox
    public AABBReference getBoundingBox() {
        return new AABBReference(instance.getBoundingBox());
    }

    // Generated from Entity::getX
    public double getX() {
        return instance.getX();
    }

    // Generated from Entity::getMaxAirSupply
    public int getMaxAirSupply() {
        return instance.getMaxAir();
    }

    // Generated from Entity::getPose
    public Statics.Poses getPose() {
        return Statics.Poses.get(instance.getPose());
    }

    // Generated from Entity::hasPose
    public boolean hasPose(Statics.Poses param0) {
        return instance.isInPose(param0.instance);
    }

    // Generated from Entity::discard
    public void discard() {
        instance.discard();
    }

    // Generated from Entity::stopRiding
    public void stopRiding() {
        instance.stopRiding();
    }

    // Generated from Entity::kill
    public void kill() {
        instance.kill();
    }

    // Generated from Entity::setPose
    public void setPose(Statics.Poses param0) {
        instance.setPose(param0.instance);
    }

    // Generated from Entity::addTag
    public boolean addTag(String param0) {
        return instance.addCommandTag(param0);
    }

    // Generated from Entity::removeTag
    public boolean removeTag(String param0) {
        return instance.removeScoreboardTag(param0);
    }

    // Generated from Entity::isPassenger
    public boolean isPassenger() {
        return instance.hasVehicle();
    }

    // Generated from Entity::ejectPassengers
    public void ejectPassengers() {
        instance.removeAllPassengers();
    }

    // Generated from Entity::turn
    public void turn(double param0, double param1) {
        instance.changeLookDirection(param0, param1);
    }

    // Generated from Entity::getVehicle
    public EntityReference<?> getVehicle() {
        return new EntityReference<>(instance.getVehicle());
    }

    // Generated from Entity::clearFire
    public void clearFire() {
        instance.extinguish();
    }

    // Generated from Entity::getXRot
    public float getXRot() {
        return instance.getPitch();
    }

    // Generated from Entity::setYRot
    public void setYRot(float param0) {
        instance.setYaw(param0);
    }

    // Generated from Entity::setXRot
    public void setXRot(float param0) {
        instance.setPitch(param0);
    }

    // Generated from Entity::setBoundingBox
    public void setBoundingBox(AABBReference param0) {
        instance.setBoundingBox(param0.instance);
    }

    // Generated from Entity::getYRot
    public float getYRot() {
        return instance.getYaw();
    }

    // Generated from Entity::getMaxFallDistance
    public int getMaxFallDistance() {
        return instance.getSafeFallDistance();
    }

    // Generated from Entity::getStringUUID
    public String getStringUUID() {
        return instance.getUuidAsString();
    }

    // Generated from Entity::hasCustomName
    public boolean hasCustomName() {
        return instance.hasCustomName();
    }

    // Generated from Entity::dismountTo
    public void dismountTo(double param0, double param1, double param2) {
        instance.requestTeleportAndDismount(param0, param1, param2);
    }

    // Generated from Entity::getMotionDirection
    public Statics.Directions getMotionDirection() {
        return Statics.Directions.get(instance.getMovementDirection());
    }

    // Generated from Entity::teleportTo
    public void teleportTo(double param0, double param1, double param2) {
        instance.requestTeleport(param0, param1, param2);
    }

    // Generated from Entity::getDirection
    public Statics.Directions getDirection() {
        return Statics.Directions.get(instance.getHorizontalFacing());
    }

    // Generated from Entity::getFirstPassenger
    public EntityReference<?> getFirstPassenger() {
        return new EntityReference<>(instance.getFirstPassenger());
    }

    // Generated from Entity::getRootVehicle
    public EntityReference<?> getRootVehicle() {
        return new EntityReference<>(instance.getRootVehicle());
    }

    // Generated from Entity::canFreeze
    public boolean canFreeze() {
        return instance.canFreeze();
    }

    // Generated from Entity::setIsInPowderSnow
    public void setIsInPowderSnow(boolean param0) {
        instance.setInPowderSnow(param0);
    }

    // Generated from Entity::isFreezing
    public boolean isFreezing() {
        return instance.shouldEscapePowderSnow();
    }

    // Generated from Entity::getBlockY
    public int getBlockY() {
        return instance.getBlockY();
    }
}
