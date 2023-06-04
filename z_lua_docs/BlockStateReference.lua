---@class BlockState : 
local blockstate = {}

---@param arg0 Block
---@return boolean
function blockstate:is(arg0) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return Vec3
function blockstate:getOffset(arg0, arg1) return nil end

---@param arg0 Rotations
---@return BlockState
function blockstate:rotate(arg0) return nil end

---@param arg0 BlockPos
---@return number
function blockstate:getSeed(arg0) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return number
function blockstate:getDestroySpeed(arg0, arg1) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 Player
---@return void
function blockstate:attack(arg0, arg1, arg2) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 Directions
---@return number
function blockstate:getSignal(arg0, arg1, arg2) return nil end

---@return boolean
function blockstate:isRandomlyTicking() return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 Entity
---@param arg3 Directions
---@return boolean
function blockstate:entityCanStandOnFace(arg0, arg1, arg2, arg3) return nil end

---@return boolean
function blockstate:hasLargeCollisionShape() return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return boolean
function blockstate:isCollisionShapeFullBlock(arg0, arg1) return nil end

---@return boolean
function blockstate:useShapeForLightOcclusion() return nil end

---@return boolean
function blockstate:hasAnalogOutputSignal() return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return number
function blockstate:getAnalogOutputSignal(arg0, arg1) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return boolean
function blockstate:propagatesSkylightDown(arg0, arg1) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return boolean
function blockstate:isRedstoneConductor(arg0, arg1) return nil end

---@return boolean
function blockstate:requiresCorrectToolForDrops() return nil end

---@return Block
function blockstate:getBlock() return nil end

---@return boolean
function blockstate:isSignalSource() return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 Entity
---@return boolean
function blockstate:entityCanStandOn(arg0, arg1, arg2) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return boolean
function blockstate:isViewBlocking(arg0, arg1) return nil end

---@return boolean
function blockstate:isAir() return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 Directions
---@return number
function blockstate:getDirectSignal(arg0, arg1, arg2) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return boolean
function blockstate:isSuffocating(arg0, arg1) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return number
function blockstate:getShadeBrightness(arg0, arg1) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@return number
function blockstate:getLightBlock(arg0, arg1) return nil end

---@param arg0 Player
---@param arg1 Level
---@param arg2 BlockPos
---@return number
function blockstate:getDestroyProgress(arg0, arg1, arg2) return nil end

---@return boolean
function blockstate:canOcclude() return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 number
---@param arg3 number
---@return boolean
function blockstate:triggerEvent(arg0, arg1, arg2, arg3) return nil end

---@param arg0 Level
---@param arg1 BlockPos
---@param arg2 Directions
---@return boolean
function blockstate:isFaceSturdy(arg0, arg1, arg2) return nil end

---@return boolean
function blockstate:hasBlockEntity() return nil end

---@return number
function blockstate:getLightEmission() return nil end
