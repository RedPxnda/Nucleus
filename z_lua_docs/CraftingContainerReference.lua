---@class CraftingContainer : 
local craftingcontainer = {}

---@return boolean
function craftingcontainer:isEmpty() return nil end

---@param arg0 number
---@return ItemStack
function craftingcontainer:getItem(arg0) return nil end

---@return number
function craftingcontainer:getHeight() return nil end

---@return number
function craftingcontainer:getWidth() return nil end

---@param arg0 Player
---@return boolean
function craftingcontainer:stillValid(arg0) return nil end

---@param arg0 number
---@param arg1 ItemStack
---@return void
function craftingcontainer:setItem(arg0, arg1) return nil end

---@return void
function craftingcontainer:clearContent() return nil end

---@param arg0 number
---@param arg1 number
---@return ItemStack
function craftingcontainer:removeItem(arg0, arg1) return nil end

---@param arg0 number
---@return ItemStack
function craftingcontainer:removeItemNoUpdate(arg0) return nil end

---@return void
function craftingcontainer:setChanged() return nil end

---@return number
function craftingcontainer:getContainerSize() return nil end
