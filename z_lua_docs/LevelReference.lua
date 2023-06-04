---@class Level : 
local level = {}

---@param arg0 BlockPos
---@return boolean
function level:isLoaded(arg0) return nil end

---@param arg0 BlockPos
---@param arg1 BlockState
---@return boolean
function level:setBlockAndUpdate(arg0, arg1) return nil end

---@param arg0 BlockPos
---@return boolean
function level:isInWorldBounds(arg0) return nil end

---@param arg0 BlockPos
---@param arg1 BlockState
---@param arg2 number
---@return boolean
function level:setBlock(arg0, arg1, arg2) return nil end

---@param arg0 BlockPos
---@param arg1 BlockState
---@param arg2 number
---@param arg3 number
---@return boolean
function level:setBlock(arg0, arg1, arg2, arg3) return nil end

---@return number
function level:getSeaLevel() return nil end

---@param arg0 BlockPos
---@return BlockEntity
function level:getBlockEntity(arg0) return nil end

---@param arg0 BlockPos
---@param arg1 boolean
---@return boolean
function level:removeBlock(arg0, arg1) return nil end

---@param arg0 BlockPos
---@param arg1 BlockState
---@param arg2 BlockState
---@param arg3 number
---@return void
function level:sendBlockUpdated(arg0, arg1, arg2, arg3) return nil end

---@param arg0 Entity
---@return boolean
function level:addFreshEntity(arg0) return nil end

---@param arg0 BlockPos
---@param arg1 Block
---@return void
function level:updateNeighborsAt(arg0, arg1) return nil end

---@param arg0 BlockPos
---@param arg1 boolean
---@param arg2 Entity
---@param arg3 number
---@return boolean
function level:destroyBlock(arg0, arg1, arg2, arg3) return nil end

---@param arg0 BlockPos
---@param arg1 Block
---@param arg2 BlockPos
---@return void
function level:neighborChanged(arg0, arg1, arg2) return nil end

---@param arg0 BlockState
---@param arg1 BlockPos
---@param arg2 Block
---@param arg3 BlockPos
---@param arg4 boolean
---@return void
function level:neighborChanged(arg0, arg1, arg2, arg3, arg4) return nil end

---@param arg0 BlockPos
---@return BlockState
function level:getBlockState(arg0) return nil end

---@param arg0 number
---@return number
function level:getSunAngle(arg0) return nil end

---@param arg0 number
---@return number
function level:getRainLevel(arg0) return nil end

---@return BlockPos
function level:getSharedSpawnPos() return nil end

---@return boolean
function level:isThundering() return nil end

---@param arg0 Entity
---@param arg1 AABB
---@param arg2 function
---@return List
function level:getEntities(arg0, arg1, arg2) return nil end

---@param arg0 BlockPos
---@param arg1 Directions
---@return number
function level:getSignal(arg0, arg1) return nil end

---@param arg0 number
---@return void
function level:setRainLevel(arg0) return nil end

---@param arg0 number
---@return number
function level:getThunderLevel(arg0) return nil end

---@param arg0 BlockPos
---@return boolean
function level:isRainingAt(arg0) return nil end

---@param arg0 BlockPos
---@return void
function level:blockEntityChanged(arg0) return nil end

---@param arg0 BlockPos
---@param arg1 Directions
---@return boolean
function level:hasSignal(arg0, arg1) return nil end

---@param arg0 BlockPos
---@return boolean
function level:hasNeighborSignal(arg0) return nil end

---@param arg0 BlockPos
---@return number
function level:getDirectSignalTo(arg0) return nil end

---@param arg0 BlockPos
---@return boolean
function level:shouldTickBlocksAt(arg0) return nil end

---@param arg0 number
---@return boolean
function level:shouldTickBlocksAt(arg0) return nil end

---@return boolean
function level:isNight() return nil end

---@param arg0 number
---@return Entity
function level:getEntity(arg0) return nil end

---@param arg0 BlockPos
---@return void
function level:removeBlockEntity(arg0) return nil end

---@return boolean
function level:isDay() return nil end

---@param arg0 Player
---@param arg1 BlockPos
---@return boolean
function level:mayInteract(arg0, arg1) return nil end

---@return number
function level:getGameTime() return nil end

---@return boolean
function level:isRaining() return nil end

---@param arg0 BlockEntity
---@return void
function level:setBlockEntity(arg0) return nil end

---@param arg0 Entity
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@param arg5 boolean
---@return void
function level:explodeByTnt(arg0, arg1, arg2, arg3, arg4, arg5) return nil end

---@param arg0 number
---@return void
function level:setThunderLevel(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@param arg5 number
---@param arg6 CompoundTag
---@return void
function level:createFireworks(arg0, arg1, arg2, arg3, arg4, arg5, arg6) return nil end

---@param arg0 Entity
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@param arg5 boolean
---@return void
function level:explodeByNone(arg0, arg1, arg2, arg3, arg4, arg5) return nil end

---@return number
function level:getSkyDarken() return nil end

---@param arg0 number
---@return void
function level:setSkyFlashTime(arg0) return nil end

---@param arg0 BlockPos
---@return boolean
function level:isHumidAt(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@return BlockPos
function level:getBlockRandomPos(arg0, arg1, arg2, arg3) return nil end

---@param arg0 Entity
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@param arg5 boolean
---@return void
function level:explodeByMob(arg0, arg1, arg2, arg3, arg4, arg5) return nil end

---@return number
function level:getDayTime() return nil end

---@param arg0 Entity
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@param arg5 boolean
---@return void
function level:explodeByBlock(arg0, arg1, arg2, arg3, arg4, arg5) return nil end

---@return number
function level:getFreeMapId() return nil end

---@param arg0 BlockPos
---@param arg1 function
---@return boolean
function level:isStateAtPosition(arg0, arg1) return nil end

---@param arg0 number
---@param arg1 BlockPos
---@param arg2 number
---@return void
function level:destroyBlockProgress(arg0, arg1, arg2) return nil end

---@param arg0 Directions
---@param arg1 BlockState
---@param arg2 BlockPos
---@param arg3 BlockPos
---@param arg4 number
---@param arg5 number
---@return void
function level:neighborShapeChanged(arg0, arg1, arg2, arg3, arg4, arg5) return nil end

---@param arg0 BlockPos
---@param arg1 Entity
---@param arg2 Directions
---@return boolean
function level:loadedAndEntityCanStandOnFace(arg0, arg1, arg2) return nil end

---@param arg0 BlockPos
---@return number
function level:getBestNeighborSignal(arg0) return nil end

---@return number
function level:getSharedSpawnAngle() return nil end

---@param arg0 BlockPos
---@param arg1 Entity
---@return boolean
function level:loadedAndEntityCanStandOn(arg0, arg1) return nil end
