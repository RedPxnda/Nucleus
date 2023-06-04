---@class Entity : 
local entity = {}

---@return Component
function entity:getName() return nil end

---@return void
function entity:remove() return nil end

---@return string
function entity:toString() return nil end

---@return Vec3
function entity:position() return nil end

---@return boolean
function entity:isAlive() return nil end

---@return number
function entity:getId() return nil end

---@return string
function entity:getType() return nil end

---@param arg0 number
---@return SlotAccess
function entity:getSlot(arg0) return nil end

---@param arg0 ResourceLocation
---@return boolean
function entity:is(arg0) return nil end

---@param arg0 Entity
---@return boolean
function entity:is(arg0) return nil end

---@param arg0 string
---@return boolean
function entity:is(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function entity:push(arg0, arg1, arg2) return nil end

---@return Component
function entity:getDisplayName() return nil end

---@return Level
function entity:getLevel() return nil end

---@param arg0 Component
---@return void
function entity:setCustomName(arg0) return nil end

---@return boolean
function entity:isPushable() return nil end

---@return UUID
function entity:getUUID() return nil end

---@return number
function entity:getAirSupply() return nil end

---@return ChunkPos
function entity:chunkPosition() return nil end

---@param arg0 Entity
---@return boolean
function entity:hasPassenger(arg0) return nil end

---@param arg0 Player
---@return void
function entity:playerTouch(arg0) return nil end

---@param arg0 DamageSource
---@return boolean
function entity:isInvulnerableTo(arg0) return nil end

---@param arg0 Player
---@param arg1 InteractionHands
---@return void
function entity:interact(arg0, arg1) return nil end

---@return Vec3
function entity:getEyePosition() return nil end

---@return Component
function entity:getCustomName() return nil end

---@param arg0 ItemStack
---@return Entity
function entity:spawnAtLocation(arg0) return nil end

---@param arg0 Entity
---@return boolean
function entity:startRiding(arg0) return nil end

---@param arg0 number
---@return void
function entity:setYHeadRot(arg0) return nil end

---@return boolean
function entity:isInWall() return nil end

---@return void
function entity:animateHurt() return nil end

---@return List
function entity:getAllSlots() return nil end

---@param arg0 number
---@return void
function entity:setYBodyRot(arg0) return nil end

---@return Vec3
function entity:getLookAngle() return nil end

---@param arg0 number
---@return void
function entity:setAirSupply(arg0) return nil end

---@return number
function entity:getBlockX() return nil end

---@return boolean
function entity:isShiftKeyDown() return nil end

---@return Vec2
function entity:getRotationVector() return nil end

---@return void
function entity:removeVehicle() return nil end

---@return Vec3
function entity:getForward() return nil end

---@param arg0 string
---@return EntityCapability
function entity:getCapability(arg0) return nil end

---@param arg0 Class
---@return EntityCapability
function entity:getCapability(arg0) return nil end

---@param arg0 ResourceLocation
---@return EntityCapability
function entity:getCapability(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function entity:setDeltaMovement(arg0, arg1, arg2) return nil end

---@param arg0 DamageSource
---@param arg1 number
---@return boolean
function entity:hurt(arg0, arg1) return nil end

---@param arg0 number
---@return void
function entity:setSecondsOnFire(arg0) return nil end

---@param arg0 Component
---@return void
function entity:sendSystemMessage(arg0) return nil end

---@return boolean
function entity:isOnGround() return nil end

---@return void
function entity:resetFallDistance() return nil end

---@param arg0 boolean
---@return void
function entity:setOnGround(arg0) return nil end

---@return boolean
function entity:isOnFire() return nil end

---@return Vec3
function entity:getDeltaMovement() return nil end

---@return boolean
function entity:isSprinting() return nil end

---@return boolean
function entity:isCrouching() return nil end

---@param arg0 Vec3
---@return void
function entity:moveTo(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function entity:moveTo(arg0, arg1, arg2) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@return void
function entity:moveTo(arg0, arg1, arg2, arg3, arg4) return nil end

---@param arg0 number
---@param arg1 Vec3
---@return void
function entity:moveRelative(arg0, arg1) return nil end

---@param arg0 boolean
---@return void
function entity:setSilent(arg0) return nil end

---@return boolean
function entity:isOnPortalCooldown() return nil end

---@param arg0 boolean
---@return void
function entity:setNoGravity(arg0) return nil end

---@return BlockState
function entity:getBlockStateOn() return nil end

---@return number
function entity:getBlockZ() return nil end

---@return boolean
function entity:isNoGravity() return nil end

---@param arg0 number
---@return void
function entity:setTicksFrozen(arg0) return nil end

---@return boolean
function entity:isInWaterOrBubble() return nil end

---@return boolean
function entity:isInWaterOrRain() return nil end

---@return number
function entity:getTicksFrozen() return nil end

---@return List
function entity:getPassengers() return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 DamageSource
---@return boolean
function entity:causeFallDamage(arg0, arg1, arg2) return nil end

---@return boolean
function entity:isInWater() return nil end

---@return boolean
function entity:isInLava() return nil end

---@return boolean
function entity:isSilent() return nil end

---@return boolean
function entity:isSwimming() return nil end

---@param arg0 Entity
---@return number
function entity:distanceTo(arg0) return nil end

---@return BlockPos
function entity:getOnPos() return nil end

---@return boolean
function entity:isUnderWater() return nil end

---@return number
function entity:getEyeY() return nil end

---@return Entity
function entity:getControllingPassenger() return nil end

---@return boolean
function entity:canSpawnSprintParticle() return nil end

---@return boolean
function entity:isInWaterRainOrBubble() return nil end

---@return number
function entity:getDimensionChangingDelay() return nil end

---@param arg0 number
---@return void
function entity:setRemainingFireTicks(arg0) return nil end

---@return boolean
function entity:canChangeDimensions() return nil end

---@param arg0 Entity
---@return boolean
function entity:hasIndirectPassenger(arg0) return nil end

---@return boolean
function entity:isCustomNameVisible() return nil end

---@param arg0 boolean
---@return void
function entity:setCustomNameVisible(arg0) return nil end

---@return number
function entity:getRemainingFireTicks() return nil end

---@return boolean
function entity:hasExactlyOnePlayerPassenger() return nil end

---@return List
function entity:getSelfAndPassengers() return nil end

---@param arg0 Entity
---@return boolean
function entity:isPassengerOfSameVehicle(arg0) return nil end

---@return boolean
function entity:hasControllingPassenger() return nil end

---@return number
function entity:getTicksRequiredToFreeze() return nil end

---@return boolean
function entity:touchingUnloadedChunk() return nil end

---@return void
function entity:resetPortalCooldown() return nil end

---@return AABB
function entity:getBoundingBox() return nil end

---@return number
function entity:getYHeadRot() return nil end

---@return Iterable
function entity:getArmorSlots() return nil end

---@return boolean
function entity:isVisuallySwimming() return nil end

---@return boolean
function entity:isVehicle() return nil end

---@param arg0 boolean
---@return void
function entity:setSprinting(arg0) return nil end

---@param arg0 boolean
---@return void
function entity:setInvulnerable(arg0) return nil end

---@return number
function entity:getX() return nil end

---@param arg0 Player
---@return boolean
function entity:isInvisibleTo(arg0) return nil end

---@return boolean
function entity:isCurrentlyGlowing() return nil end

---@return boolean
function entity:isVisuallyCrawling() return nil end

---@return boolean
function entity:isInvisible() return nil end

---@param arg0 Entity
---@return boolean
function entity:isAlliedTo(arg0) return nil end

---@return boolean
function entity:isAttackable() return nil end

---@param arg0 boolean
---@return void
function entity:setShiftKeyDown(arg0) return nil end

---@param arg0 boolean
---@return void
function entity:setInvisible(arg0) return nil end

---@return number
function entity:getEyeHeight() return nil end

---@return boolean
function entity:isSpectator() return nil end

---@param arg0 Vec3
---@return void
function entity:setPos(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function entity:setPos(arg0, arg1, arg2) return nil end

---@return number
function entity:getY() return nil end

---@return Iterable
function entity:getHandSlots() return nil end

---@return string
function entity:getScoreboardName() return nil end

---@return boolean
function entity:isFullyFrozen() return nil end

---@param arg0 EquipmentSlots
---@param arg1 ItemStack
---@return void
function entity:setItemSlot(arg0, arg1) return nil end

---@return number
function entity:getPercentFrozen() return nil end

---@return number
function entity:getZ() return nil end

---@return void
function entity:unRide() return nil end

---@return boolean
function entity:isInvulnerable() return nil end

---@return number
function entity:getTeamColor() return nil end

---@param arg0 number
---@param arg1 number
---@return void
function entity:turn(arg0, arg1) return nil end

---@param arg0 number
---@return void
function entity:setYRot(arg0) return nil end

---@return number
function entity:getYRot() return nil end

---@param arg0 Poses
---@return boolean
function entity:hasPose(arg0) return nil end

---@return void
function entity:kill() return nil end

---@return number
function entity:getMaxFallDistance() return nil end

---@return string
function entity:getStringUUID() return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function entity:dismountTo(arg0, arg1, arg2) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function entity:teleportTo(arg0, arg1, arg2) return nil end

---@return Entity
function entity:getFirstPassenger() return nil end

---@param arg0 Poses
---@return void
function entity:setPose(arg0) return nil end

---@return Entity
function entity:getRootVehicle() return nil end

---@param arg0 string
---@return boolean
function entity:addTag(arg0) return nil end

---@return Entity
function entity:getVehicle() return nil end

---@return boolean
function entity:isPassenger() return nil end

---@return number
function entity:getXRot() return nil end

---@return void
function entity:clearFire() return nil end

---@return Poses
function entity:getPose() return nil end

---@param arg0 string
---@return boolean
function entity:removeTag(arg0) return nil end

---@param arg0 AABB
---@return void
function entity:setBoundingBox(arg0) return nil end

---@return Directions
function entity:getMotionDirection() return nil end

---@return number
function entity:getMaxAirSupply() return nil end

---@return void
function entity:stopRiding() return nil end

---@return void
function entity:discard() return nil end

---@return boolean
function entity:hasCustomName() return nil end

---@return void
function entity:ejectPassengers() return nil end

---@param arg0 number
---@return void
function entity:setXRot(arg0) return nil end

---@return Directions
function entity:getDirection() return nil end

---@return boolean
function entity:isFreezing() return nil end

---@return void
function entity:burnWithLava() return nil end

---@param arg0 boolean
---@return void
function entity:setIsInPowderSnow(arg0) return nil end

---@return Map
function entity:getStats() return nil end

---@return boolean
function entity:canFreeze() return nil end

---@return number
function entity:getBlockY() return nil end
