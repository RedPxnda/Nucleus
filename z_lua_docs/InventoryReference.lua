---@class Inventory : 
local inventory = {}

---@return Component
function inventory:getName() return nil end

---@param arg0 ItemStack
---@return boolean
function inventory:add(arg0) return nil end

---@param arg0 number
---@param arg1 ItemStack
---@return boolean
function inventory:add(arg0, arg1) return nil end

---@return boolean
function inventory:isEmpty() return nil end

---@param arg0 ItemStack
---@return boolean
function inventory:contains(arg0) return nil end

---@param arg0 ListTag
---@return ListTag
function inventory:save(arg0) return nil end

---@param arg0 Inventory
---@return void
function inventory:replaceWith(arg0) return nil end

---@param arg0 number
---@return ItemStack
function inventory:getItem(arg0) return nil end

---@return void
function inventory:dropAll() return nil end

---@param arg0 Player
---@return boolean
function inventory:stillValid(arg0) return nil end

---@return void
function inventory:clearContent() return nil end

---@return number
function inventory:getTimesChanged() return nil end

---@return void
function inventory:setChanged() return nil end

---@param arg0 BlockState
---@return number
function inventory:getDestroySpeed(arg0) return nil end

---@param arg0 DamageSource
---@param arg1 number
---@param arg2 number[]
---@return void
function inventory:hurtArmor(arg0, arg1, arg2) return nil end

---@param arg0 boolean
---@return ItemStack
function inventory:removeFromSelected(arg0) return nil end

---@param arg0 number
---@return void
function inventory:swapPaint(arg0) return nil end

---@param arg0 number
---@return ItemStack
function inventory:getArmor(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@return ItemStack
function inventory:removeItem(arg0, arg1) return nil end

---@param arg0 ItemStack
---@return void
function inventory:removeItem(arg0) return nil end

---@param arg0 ItemStack
---@return void
function inventory:setPickedItem(arg0) return nil end

---@return number
function inventory:getFreeSlot() return nil end

---@param arg0 number
---@return void
function inventory:pickSlot(arg0) return nil end

---@return number
function inventory:getContainerSize() return nil end

---@param arg0 number
---@return ItemStack
function inventory:removeItemNoUpdate(arg0) return nil end

---@param arg0 number
---@param arg1 ItemStack
---@return void
function inventory:setItem(arg0, arg1) return nil end

---@return ItemStack
function inventory:getSelected() return nil end

---@param arg0 ItemStack
---@return number
function inventory:findSlotMatchingUnusedItem(arg0) return nil end

---@return number
function inventory:getSuitableHotbarSlot() return nil end

---@param arg0 ItemStack
---@return void
function inventory:placeItemBackInInventory(arg0) return nil end

---@param arg0 ItemStack
---@param arg1 boolean
---@return void
function inventory:placeItemBackInInventory(arg0, arg1) return nil end

---@param arg0 ItemStack
---@return number
function inventory:findSlotMatchingItem(arg0) return nil end

---@param arg0 ItemStack
---@return number
function inventory:getSlotWithRemainingSpace(arg0) return nil end
