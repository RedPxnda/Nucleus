---@class BlockPos : 
local blockpos = {}

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return BlockPos
function blockpos:offset(arg0, arg1, arg2) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return BlockPos
function blockpos:offset(arg0, arg1, arg2) return nil end

---@return number
function blockpos:x() return nil end

---@return number
function blockpos:z() return nil end

---@return number
function blockpos:y() return nil end

---@param arg0 number
---@return BlockPos
function blockpos:multiply(arg0) return nil end

---@param arg0 Rotations
---@return BlockPos
function blockpos:rotate(arg0) return nil end

---@param arg0 Directions
---@param arg1 number
---@return BlockPos
function blockpos:relative(arg0, arg1) return nil end

---@param arg0 Directions
---@return BlockPos
function blockpos:relative(arg0) return nil end

---@param arg0 Axes
---@param arg1 number
---@return BlockPos
function blockpos:relative(arg0, arg1) return nil end

---@return BlockPos
function blockpos:east() return nil end

---@param arg0 number
---@return BlockPos
function blockpos:east(arg0) return nil end

---@param arg0 number
---@return BlockPos
function blockpos:atY(arg0) return nil end

---@param arg0 number
---@return BlockPos
function blockpos:south(arg0) return nil end

---@return BlockPos
function blockpos:south() return nil end

---@return BlockPos
function blockpos:immutable() return nil end

---@return number
function blockpos:asLong() return nil end

---@param arg0 number
---@return BlockPos
function blockpos:above(arg0) return nil end

---@return BlockPos
function blockpos:above() return nil end

---@param arg0 number
---@return BlockPos
function blockpos:below(arg0) return nil end

---@return BlockPos
function blockpos:below() return nil end

---@param arg0 number
---@return BlockPos
function blockpos:north(arg0) return nil end

---@return BlockPos
function blockpos:north() return nil end

---@return BlockPos
function blockpos:west() return nil end

---@param arg0 number
---@return BlockPos
function blockpos:west(arg0) return nil end
