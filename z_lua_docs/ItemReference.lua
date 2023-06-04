---@class Item : 
local item = {}

---@param arg0 ItemStack
---@return Component
function item:getName(arg0) return nil end

---@return string
function item:toString() return nil end

---@param arg0 Level
---@param arg1 Player
---@param arg2 InteractionHands
---@return void
function item:use(arg0, arg1, arg2) return nil end

---@param arg0 ItemStack
---@param arg1 BlockState
---@return number
function item:getDestroySpeed(arg0, arg1) return nil end

---@return string
function item:getDescriptionId() return nil end

---@param arg0 ItemStack
---@return string
function item:getDescriptionId(arg0) return nil end

---@return FoodProperties
function item:getFoodProperties() return nil end

---@return boolean
function item:isFireResistant() return nil end

---@param arg0 BlockState
---@param arg1 Level
---@param arg2 BlockPos
---@param arg3 Player
---@return boolean
function item:canAttackBlock(arg0, arg1, arg2, arg3) return nil end

---@return boolean
function item:isEdible() return nil end

---@param arg0 CompoundTag
---@return void
function item:verifyTagAfterLoad(arg0) return nil end

---@param arg0 ItemStack
---@param arg1 Player
---@param arg2 LivingEntity
---@param arg3 InteractionHands
---@return void
function item:interactLivingEntity(arg0, arg1, arg2, arg3) return nil end

---@param arg0 ItemStack
---@return boolean
function item:isFoil(arg0) return nil end

---@return number
function item:getMaxStackSize() return nil end

---@param arg0 ItemStack
---@return Rarities
function item:getRarity(arg0) return nil end

---@param arg0 ItemStack
---@param arg1 LivingEntity
---@param arg2 LivingEntity
---@return boolean
function item:hurtEnemy(arg0, arg1, arg2) return nil end

---@param arg0 ItemStack
---@param arg1 ItemStack
---@return boolean
function item:isValidRepairItem(arg0, arg1) return nil end

---@param arg0 DamageSource
---@return boolean
function item:canBeHurtBy(arg0) return nil end

---@param arg0 ItemStack
---@return UseAnims
function item:getUseAnimation(arg0) return nil end

---@return Component
function item:getDescription() return nil end

---@return number
function item:getMaxDamage() return nil end

---@return boolean
function item:canBeDepleted() return nil end

---@param arg0 ItemStack
---@return boolean
function item:isEnchantable(arg0) return nil end

---@param arg0 ItemStack
---@param arg1 Level
---@param arg2 BlockState
---@param arg3 BlockPos
---@param arg4 LivingEntity
---@return boolean
function item:mineBlock(arg0, arg1, arg2, arg3, arg4) return nil end

---@return ItemStack
function item:getDefaultInstance() return nil end

---@return number
function item:getEnchantmentValue() return nil end

---@param arg0 BlockState
---@return boolean
function item:isCorrectToolForDrops(arg0) return nil end
