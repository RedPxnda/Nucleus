---@class Block : 
local block = {}

---@return Component
function block:getName() return nil end

---@return string
function block:toString() return nil end

---@return string
function block:getDescriptionId() return nil end

---@return number
function block:getSpeedFactor() return nil end

---@return number
function block:getFriction() return nil end

---@return boolean
function block:hasDynamicShape() return nil end

---@return Item
function block:asItem() return nil end

---@param arg0 BlockState
---@return boolean
function block:isRandomlyTicking(arg0) return nil end

---@return number
function block:getJumpFactor() return nil end

---@return number
function block:getExplosionResistance() return nil end

---@return boolean
function block:isPossibleToRespawnInThis() return nil end
