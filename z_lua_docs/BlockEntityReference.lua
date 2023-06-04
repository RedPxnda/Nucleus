---@class BlockEntity : 
local blockentity = {}

---@return Level
function blockentity:getLevel() return nil end

---@param arg0 Level
---@return void
function blockentity:setLevel(arg0) return nil end

---@return CompoundTag
function blockentity:getUpdateTag() return nil end

---@return boolean
function blockentity:isRemoved() return nil end

---@return void
function blockentity:setRemoved() return nil end

---@return BlockPos
function blockentity:getBlockPos() return nil end

---@return boolean
function blockentity:hasLevel() return nil end

---@return void
function blockentity:setChanged() return nil end

---@param arg0 ItemStack
---@return void
function blockentity:saveToItem(arg0) return nil end

---@return CompoundTag
function blockentity:saveWithId() return nil end

---@return BlockState
function blockentity:getBlockState() return nil end

---@return CompoundTag
function blockentity:saveWithoutMetadata() return nil end

---@return CompoundTag
function blockentity:saveWithFullMetadata() return nil end
