---@class Player : LivingEntity
local player = {}

---@param arg0 number
---@return SlotAccess
function player:getSlot(arg0) return nil end

---@return Component
function player:getDisplayName() return nil end

---@param arg0 ItemStack
---@param arg1 boolean
---@param arg2 boolean
---@return Entity
function player:drop(arg0, arg1, arg2) return nil end

---@param arg0 ItemStack
---@param arg1 boolean
---@return Entity
function player:drop(arg0, arg1) return nil end

---@return void
function player:removeVehicle() return nil end

---@param arg0 Component
---@param arg1 boolean
---@return void
---@overload fun(arg0: Component): void
function player:sendSystemMessage(arg0, arg1) return nil end

---@param arg0 string
---@param arg1 boolean
---@return void
---@overload fun(arg0: Component): void
function player:sendSystemMessage(arg0, arg1) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 DamageSource
---@return boolean
function player:causeFallDamage(arg0, arg1, arg2) return nil end

---@return boolean
function player:isSwimming() return nil end

---@return number
function player:getDimensionChangingDelay() return nil end

---@param arg0 number
---@return void
function player:setRemainingFireTicks(arg0) return nil end

---@param arg0 number
---@return void
function player:causeFoodExhaustion(arg0) return nil end

---@param arg0 Component
---@param arg1 boolean
---@return void
function player:displayClientMessage(arg0, arg1) return nil end

---@return number
function player:getAbsorptionAmount() return nil end

---@return void
function player:resetAttackStrengthTicker() return nil end

---@return CompoundTag
function player:getShoulderEntityRight() return nil end

---@return number
function player:getExperienceReward() return nil end

---@param arg0 number
---@return void
function player:setAbsorptionAmount(arg0) return nil end

---@return CompoundTag
function player:getShoulderEntityLeft() return nil end

---@param arg0 number
---@return number
function player:getAttackStrengthScale(arg0) return nil end

---@param arg0 BlockState
---@return boolean
function player:hasCorrectToolForDrops(arg0) return nil end

---@return number
function player:getCurrentItemAttackStrengthDelay() return nil end

---@return boolean
function player:tryToStartFallFlying() return nil end

---@param arg0 number
---@return void
function player:giveExperienceLevels(arg0) return nil end

---@param arg0 number
---@return void
function player:giveExperiencePoints(arg0) return nil end

---@return number
function player:getXpNeededForNextLevel() return nil end

---@param arg0 CompoundTag
---@return boolean
function player:setEntityOnShoulder(arg0) return nil end

---@return boolean
function player:canUseGameMasterBlocks() return nil end

---@param arg0 boolean
---@return void
function player:setReducedDebugInfo(arg0) return nil end

---@return Optional
function player:getLastDeathLocation() return nil end

---@return number
function player:getSpeed() return nil end

---@return boolean
function player:isSpectator() return nil end

---@param arg0 DamageSource
---@return void
function player:die(arg0) return nil end

---@return void
function player:stopSleeping() return nil end

---@param arg0 EquipmentSlots
---@return ItemStack
function player:getItemBySlot(arg0) return nil end

---@return Inventory
function player:getInventory() return nil end

---@return number
function player:getScore() return nil end

---@param arg0 number
---@return void
function player:increaseScore(arg0) return nil end

---@param arg0 BlockState
---@return number
function player:getDestroySpeed(arg0) return nil end

---@return void
function player:rideTick() return nil end

---@return void
function player:closeContainer() return nil end

---@param arg0 number
---@return void
function player:setScore(arg0) return nil end

---@return number
function player:getSleepTimer() return nil end

---@return void
function player:jumpFromGround() return nil end

---@return void
function player:stopFallFlying() return nil end

---@param arg0 ResourceLocation
---@return void
function player:awardStat(arg0) return nil end

---@param arg0 ResourceLocation
---@param arg1 number
---@return void
function player:awardStat(arg0, arg1) return nil end

---@return number
function player:getPortalWaitTime() return nil end

---@return void
function player:aiStep() return nil end

---@return boolean
function player:mayBuild() return nil end

---@return void
function player:startFallFlying() return nil end

---@param arg0 boolean
---@param arg1 boolean
---@return void
function player:stopSleepInBed(arg0, arg1) return nil end

---@return boolean
function player:isHurt() return nil end

---@param arg0 boolean
---@return boolean
function player:canEat(arg0) return nil end

---@return boolean
function player:isScoping() return nil end

---@param arg0 ItemStack
---@return boolean
function player:addItem(arg0) return nil end

---@return ItemCooldowns
function player:getCooldowns() return nil end

---@param arg0 Entity
---@return void
function player:magicCrit(arg0) return nil end

---@param arg0 BlockPos
---@param arg1 Directions
---@param arg2 ItemStack
---@return boolean
function player:mayUseItemAt(arg0, arg1, arg2) return nil end

---@return number
function player:getEnchantmentSeed() return nil end

---@return boolean
function player:isLocalPlayer() return nil end

---@param arg0 Entity
---@param arg1 InteractionHands
---@return void
function player:interactOn(arg0, arg1) return nil end

---@return boolean
function player:hasContainerOpen() return nil end

---@param arg0 Entity
---@return void
function player:crit(arg0) return nil end

---@param arg0 Player
---@return boolean
function player:canHarmPlayer(arg0) return nil end

---@return void
function player:sweepAttack() return nil end

---@param arg0 ResourceLocation[]
---@return void
function player:awardRecipes(arg0) return nil end

---@param arg0 boolean
---@return void
function player:disableShield(arg0) return nil end

---@return number
function player:getLuck() return nil end

---@param arg0 Entity
---@return void
function player:attack(arg0) return nil end

---@param arg0 BlockPos
---@return void
function player:startSleepInBed(arg0) return nil end

---@return boolean
function player:isCreative() return nil end
