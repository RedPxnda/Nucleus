---@class ChunkPos : 
local chunkpos = {}

---@param arg0 Object
---@return boolean
function chunkpos:equals(arg0) return nil end

---@return string
function chunkpos:toString() return nil end

---@param arg0 number
---@return number
function chunkpos:getBlockX(arg0) return nil end

---@param arg0 number
---@return number
function chunkpos:getBlockZ(arg0) return nil end

---@param arg0 number
---@return BlockPos
function chunkpos:getMiddleBlockPosition(arg0) return nil end

---@param arg0 ChunkPos
---@return number
function chunkpos:getChessboardDistance(arg0) return nil end

---@return number
function chunkpos:getRegionLocalX() return nil end

---@return BlockPos
function chunkpos:getWorldPosition() return nil end

---@return number
function chunkpos:getMiddleBlockZ() return nil end

---@return number
function chunkpos:getMinBlockX() return nil end

---@return number
function chunkpos:getMiddleBlockX() return nil end

---@return number
function chunkpos:getMaxBlockX() return nil end

---@return number
function chunkpos:getMinBlockZ() return nil end

---@return number
function chunkpos:toLong() return nil end

---@return number
function chunkpos:getMaxBlockZ() return nil end

---@return number
function chunkpos:getRegionZ() return nil end

---@return number
function chunkpos:getRegionX() return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return BlockPos
function chunkpos:getBlockAt(arg0, arg1, arg2) return nil end

---@return number
function chunkpos:getRegionLocalZ() return nil end
