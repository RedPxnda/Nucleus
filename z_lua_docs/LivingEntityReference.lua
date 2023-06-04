---@class LivingEntity : Entity
local livingentity = {}

---@return boolean
function livingentity:isAlive() return nil end

---@param arg0 number
---@return SlotAccess
function livingentity:getSlot(arg0) return nil end

---@param arg0 Entity
---@return void
---@overload fun(arg0: number, arg1: number, arg2: number): void
function livingentity:push(arg0) return nil end

---@return boolean
function livingentity:isPushable() return nil end

---@param arg0 number
---@return void
function livingentity:setYHeadRot(arg0) return nil end

---@return boolean
function livingentity:isInWall() return nil end

---@return void
function livingentity:animateHurt() return nil end

---@param arg0 number
---@return void
function livingentity:setYBodyRot(arg0) return nil end

---@param arg0 DamageSource
---@param arg1 number
---@return boolean
function livingentity:hurt(arg0, arg1) return nil end

---@param arg0 boolean
---@return void
function livingentity:setOnGround(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 DamageSource
---@return boolean
function livingentity:causeFallDamage(arg0, arg1, arg2) return nil end

---@return boolean
function livingentity:isInvertedHealAndHarm() return nil end

---@return boolean
function livingentity:canSpawnSoulSpeedParticle() return nil end

---@return number
function livingentity:getArmorCoverPercentage() return nil end

---@return number
function livingentity:getUseItemRemainingTicks() return nil end

---@return number
function livingentity:getAbsorptionAmount() return nil end

---@param arg0 DamageSource
---@return boolean
function livingentity:isDamageSourceBlocked(arg0) return nil end

---@return Map
function livingentity:getActiveEffectsMap() return nil end

---@return DamageSource
function livingentity:getLastDamageSource() return nil end

---@return boolean
function livingentity:wasExperienceConsumed() return nil end

---@return number
function livingentity:getLastHurtMobTimestamp() return nil end

---@return boolean
function livingentity:shouldDropExperience() return nil end

---@param arg0 ResourceLocation
---@return MobEffectInstance
function livingentity:removeEffectNoUpdate(arg0) return nil end

---@param arg0 Entity
---@return number
function livingentity:getVisibilityPercent(arg0) return nil end

---@return boolean
function livingentity:isSuppressingSlidingDownLadder() return nil end

---@return boolean
function livingentity:isAffectedByPotions() return nil end

---@param arg0 BlockPos
---@param arg1 boolean
---@return void
function livingentity:setRecordPlayingNearby(arg0, arg1) return nil end

---@return boolean
function livingentity:canBreatheUnderwater() return nil end

---@param arg0 Player
---@return void
function livingentity:setLastHurtByPlayer(arg0) return nil end

---@return boolean
function livingentity:shouldDiscardFriction() return nil end

---@return number
function livingentity:getLastHurtByMobTimestamp() return nil end

---@return number
function livingentity:getExperienceReward() return nil end

---@param arg0 number
---@return void
function livingentity:setAbsorptionAmount(arg0) return nil end

---@return boolean
function livingentity:shouldShowName() return nil end

---@return void
function livingentity:clearSleepingPos() return nil end

---@return number
function livingentity:getJumpBoostPower() return nil end

---@return Directions
function livingentity:getBedOrientation() return nil end

---@param arg0 BlockPos
---@return void
function livingentity:startSleeping(arg0) return nil end

---@return boolean
function livingentity:canDisableShield() return nil end

---@return boolean
function livingentity:isSensitiveToWater() return nil end

---@return ItemStack
function livingentity:getUseItem() return nil end

---@return boolean
function livingentity:isUsingItem() return nil end

---@return boolean
function livingentity:attackable() return nil end

---@return number
function livingentity:getSpeed() return nil end

---@param arg0 InteractionHands
---@param arg1 ItemStack
---@return void
function livingentity:setItemInHand(arg0, arg1) return nil end

---@param arg0 InteractionHands
---@return void
function livingentity:startUsingItem(arg0) return nil end

---@param arg0 EquipmentSlots
---@return boolean
function livingentity:hasItemInSlot(arg0) return nil end

---@param arg0 number
---@return void
function livingentity:setSpeed(arg0) return nil end

---@return void
function livingentity:stopUsingItem() return nil end

---@return number
function livingentity:getFallFlyingTicks() return nil end

---@return InteractionHands
function livingentity:getUsedItemHand() return nil end

---@param arg0 Level
---@param arg1 ItemStack
---@return ItemStack
function livingentity:eat(arg0, arg1) return nil end

---@return number
function livingentity:getTicksUsingItem() return nil end

---@param arg0 ItemStack
---@return boolean
function livingentity:canTakeItem(arg0) return nil end

---@return void
function livingentity:releaseUsingItem() return nil end

---@return number
function livingentity:getYHeadRot() return nil end

---@return boolean
function livingentity:isVisuallySwimming() return nil end

---@param arg0 boolean
---@return void
function livingentity:setSprinting(arg0) return nil end

---@return boolean
function livingentity:isCurrentlyGlowing() return nil end

---@param arg0 EquipmentSlots
---@param arg1 ItemStack
---@return void
function livingentity:setItemSlot(arg0, arg1) return nil end

---@return void
function livingentity:kill() return nil end

---@return void
function livingentity:stopRiding() return nil end

---@return boolean
function livingentity:canFreeze() return nil end

---@param arg0 ResourceLocation
---@return boolean
function livingentity:hasEffect(arg0) return nil end

---@param arg0 ResourceLocation
---@return MobEffectInstance
function livingentity:getEffect(arg0) return nil end

---@param arg0 MobEffectInstance
---@return boolean
function livingentity:addEffect(arg0) return nil end

---@param arg0 MobEffectInstance
---@return boolean
function livingentity:canBeAffected(arg0) return nil end

---@return ResourceLocation
function livingentity:getLootTable() return nil end

---@param arg0 LivingEntity
---@param arg1 TargetingConditions
---@return boolean
function livingentity:canAttack(arg0, arg1) return nil end

---@param arg0 LivingEntity
---@return boolean
function livingentity:canAttack(arg0) return nil end

---@return List
function livingentity:getActiveEffects() return nil end

---@param arg0 DamageSource
---@return void
function livingentity:die(arg0) return nil end

---@return boolean
function livingentity:isBlocking() return nil end

---@return LivingEntity
function livingentity:getKillCredit() return nil end

---@return number
function livingentity:getVoicePitch() return nil end

---@return boolean
function livingentity:canBeSeenAsEnemy() return nil end

---@param arg0 number
---@return void
function livingentity:heal(arg0) return nil end

---@return boolean
function livingentity:isSleeping() return nil end

---@param arg0 number
---@return void
function livingentity:setHealth(arg0) return nil end

---@param arg0 ResourceLocation
---@return boolean
function livingentity:removeEffect(arg0) return nil end

---@return Optional
function livingentity:getSleepingPos() return nil end

---@return void
function livingentity:stopSleeping() return nil end

---@return void
function livingentity:skipDropExperience() return nil end

---@param arg0 BlockPos
---@return void
function livingentity:setSleepingPos(arg0) return nil end

---@return boolean
function livingentity:canBeSeenByAnyone() return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return void
function livingentity:knockback(arg0, arg1, arg2) return nil end

---@param arg0 InteractionHands
---@return ItemStack
function livingentity:getItemInHand(arg0) return nil end

---@return number
function livingentity:getMaxHealth() return nil end

---@param arg0 number
---@return number
function livingentity:getSwimAmount(arg0) return nil end

---@return boolean
function livingentity:rideableUnderWater() return nil end

---@return boolean
function livingentity:removeAllEffects() return nil end

---@param arg0 LivingEntity
---@return void
function livingentity:setLastHurtByMob(arg0) return nil end

---@return LivingEntity
function livingentity:getLastHurtMob() return nil end

---@return boolean
function livingentity:isBaby() return nil end

---@return boolean
function livingentity:isAutoSpinAttack() return nil end

---@param arg0 number
---@return void
function livingentity:setArrowCount(arg0) return nil end

---@return boolean
function livingentity:isFallFlying() return nil end

---@return number
function livingentity:getArmorValue() return nil end

---@return number
function livingentity:getStingerCount() return nil end

---@return ItemStack
function livingentity:getMainHandItem() return nil end

---@param arg0 EquipmentSlots
---@return ItemStack
function livingentity:getItemBySlot(arg0) return nil end

---@param arg0 boolean
---@return void
function livingentity:setDiscardFriction(arg0) return nil end

---@return boolean
function livingentity:isDeadOrDying() return nil end

---@param arg0 number
---@return void
function livingentity:setNoActionTime(arg0) return nil end

---@param arg0 number
---@return void
function livingentity:setStingerCount(arg0) return nil end

---@return ItemStack
function livingentity:getOffhandItem() return nil end

---@param arg0 Entity
---@return boolean
function livingentity:hasLineOfSight(arg0) return nil end

---@return number
function livingentity:getNoActionTime() return nil end

---@param arg0 number
---@return number
function livingentity:getViewYRot(arg0) return nil end

---@param arg0 InteractionHands
---@param arg1 boolean
---@return void
function livingentity:swing(arg0, arg1) return nil end

---@param arg0 InteractionHands
---@return void
function livingentity:swing(arg0) return nil end

---@param arg0 Entity
---@return void
function livingentity:setLastHurtMob(arg0) return nil end

---@return number
function livingentity:getArrowCount() return nil end

---@return number
function livingentity:getHealth() return nil end

---@return LivingEntity
function livingentity:getLastHurtByMob() return nil end

---@param arg0 function
---@return boolean
function livingentity:isHolding(arg0) return nil end

---@param arg0 Item
---@return boolean
function livingentity:isHolding(arg0) return nil end

---@return boolean
function livingentity:isLeftHanded() return nil end
