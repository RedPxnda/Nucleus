---@class ItemStack : 
local itemstack = {}

---@return string
function itemstack:toString() return nil end

---@return boolean
function itemstack:isEmpty() return nil end

---@param arg0 number
---@return ItemStack
function itemstack:split(arg0) return nil end

---@param arg0 CompoundTag
---@return CompoundTag
function itemstack:save(arg0) return nil end

---@return ItemStack
function itemstack:copy() return nil end

---@param arg0 Item
---@return boolean
function itemstack:is(arg0) return nil end

---@param arg0 number
---@return void
function itemstack:grow(arg0) return nil end

---@return number
function itemstack:getCount() return nil end

---@return Item
function itemstack:getItem() return nil end

---@return CompoundTag
function itemstack:getTag() return nil end

---@param arg0 Level
---@param arg1 Player
---@param arg2 InteractionHands
---@return void
function itemstack:use(arg0, arg1, arg2) return nil end

---@return Component
function itemstack:getDisplayName() return nil end

---@param arg0 BlockState
---@return number
function itemstack:getDestroySpeed(arg0) return nil end

---@return string
function itemstack:getDescriptionId() return nil end

---@param arg0 CompoundTag
---@return void
function itemstack:setTag(arg0) return nil end

---@return boolean
function itemstack:isEnchanted() return nil end

---@return boolean
function itemstack:isFramed() return nil end

---@return Entity
function itemstack:getFrame() return nil end

---@return number
function itemstack:getBaseRepairCost() return nil end

---@param arg0 number
---@return void
function itemstack:setRepairCost(arg0) return nil end

---@return boolean
function itemstack:isEdible() return nil end

---@param arg0 EquipmentSlots
---@return Multimap
function itemstack:getAttributeModifiers(arg0) return nil end

---@param arg0 Player
---@param arg1 LivingEntity
---@param arg2 InteractionHands
---@return void
function itemstack:interactLivingEntity(arg0, arg1, arg2) return nil end

---@param arg0 string
---@return CompoundTag
function itemstack:getOrCreateTagElement(arg0) return nil end

---@return number
function itemstack:getMaxStackSize() return nil end

---@return Rarities
function itemstack:getRarity() return nil end

---@param arg0 LivingEntity
---@param arg1 Player
---@return void
function itemstack:hurtEnemy(arg0, arg1) return nil end

---@return UseAnims
function itemstack:getUseAnimation() return nil end

---@return number
function itemstack:getMaxDamage() return nil end

---@return boolean
function itemstack:isEnchantable() return nil end

---@param arg0 Level
---@param arg1 BlockState
---@param arg2 BlockPos
---@param arg3 Player
---@return void
function itemstack:mineBlock(arg0, arg1, arg2, arg3) return nil end

---@param arg0 number
---@return void
function itemstack:setDamageValue(arg0) return nil end

---@param arg0 number
---@return void
function itemstack:setPopTime(arg0) return nil end

---@return CompoundTag
function itemstack:getOrCreateTag() return nil end

---@return number
function itemstack:getPopTime() return nil end

---@return boolean
function itemstack:hasCustomHoverName() return nil end

---@param arg0 string
---@param arg1 Tag
---@return void
function itemstack:addTagElement(arg0, arg1) return nil end

---@param arg0 ResourceLocation
---@param arg1 number
---@return void
function itemstack:enchant(arg0, arg1) return nil end

---@param arg0 number
---@return void
function itemstack:shrink(arg0) return nil end

---@param arg0 Level
---@param arg1 LivingEntity
---@param arg2 number
---@return void
function itemstack:releaseUsing(arg0, arg1, arg2) return nil end

---@return number
function itemstack:getDamageValue() return nil end

---@return boolean
function itemstack:isStackable() return nil end

---@param arg0 string
---@return void
function itemstack:removeTagKey(arg0) return nil end

---@param arg0 string
---@return CompoundTag
function itemstack:getTagElement(arg0) return nil end

---@return boolean
function itemstack:isDamageableItem() return nil end

---@param arg0 ItemStack
---@return boolean
function itemstack:sameItem(arg0) return nil end

---@return boolean
function itemstack:isDamaged() return nil end

---@return boolean
function itemstack:hasTag() return nil end

---@return number
function itemstack:getUseDuration() return nil end

---@return boolean
function itemstack:hasFoil() return nil end

---@return void
function itemstack:resetHoverName() return nil end

---@return ListTag
function itemstack:getEnchantmentTags() return nil end

---@param arg0 number
---@return void
function itemstack:setCount(arg0) return nil end

---@param arg0 BlockState
---@return boolean
function itemstack:isCorrectToolForDrops(arg0) return nil end
