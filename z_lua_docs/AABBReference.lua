---@class AABB : 
local aabb = {}

---@param arg0 Object
---@return boolean
function aabb:equals(arg0) return nil end

---@return string
function aabb:toString() return nil end

---@param arg0 Axes
---@return number
function aabb:min(arg0) return nil end

---@param arg0 Axes
---@return number
function aabb:max(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return AABB
function aabb:inflate(arg0, arg1, arg2) return nil end

---@param arg0 number
---@return AABB
function aabb:inflate(arg0) return nil end

---@param arg0 Vec3
---@return boolean
function aabb:contains(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return boolean
function aabb:contains(arg0, arg1, arg2) return nil end

---@return number
function aabb:getSize() return nil end

---@param arg0 Vec3
---@return AABB
function aabb:move(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return AABB
function aabb:move(arg0, arg1, arg2) return nil end

---@param arg0 BlockPos
---@return AABB
function aabb:move(arg0) return nil end

---@param arg0 number
---@return AABB
function aabb:deflate(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return AABB
function aabb:deflate(arg0, arg1, arg2) return nil end

---@param arg0 number
---@return AABB
function aabb:setMinY(arg0) return nil end

---@param arg0 AABB
---@return boolean
function aabb:intersects(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@param arg3 number
---@param arg4 number
---@param arg5 number
---@return boolean
function aabb:intersects(arg0, arg1, arg2, arg3, arg4, arg5) return nil end

---@param arg0 Vec3
---@param arg1 Vec3
---@return boolean
function aabb:intersects(arg0, arg1) return nil end

---@return Vec3
function aabb:getCenter() return nil end

---@param arg0 AABB
---@return AABB
function aabb:minmax(arg0) return nil end

---@return boolean
function aabb:hasNaN() return nil end

---@return number
function aabb:getYsize() return nil end

---@return number
function aabb:getZsize() return nil end

---@param arg0 number
---@return AABB
function aabb:setMaxY(arg0) return nil end

---@param arg0 Vec3
---@param arg1 Vec3
---@return Optional
function aabb:clip(arg0, arg1) return nil end

---@return number
function aabb:getXsize() return nil end

---@param arg0 number
---@return AABB
function aabb:setMinX(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return AABB
function aabb:contract(arg0, arg1, arg2) return nil end

---@param arg0 Vec3
---@return AABB
function aabb:expandTowards(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return AABB
function aabb:expandTowards(arg0, arg1, arg2) return nil end

---@param arg0 number
---@return AABB
function aabb:setMaxX(arg0) return nil end

---@param arg0 AABB
---@return AABB
function aabb:intersect(arg0) return nil end

---@param arg0 number
---@return AABB
function aabb:setMinZ(arg0) return nil end

---@param arg0 number
---@return AABB
function aabb:setMaxZ(arg0) return nil end
