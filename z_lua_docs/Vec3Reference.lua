---@class Vec3 : 
local vec3 = {}

---@param arg0 Vec3
---@return Vec3
function vec3:add(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return Vec3
function vec3:add(arg0, arg1, arg2) return nil end

---@param arg0 Axes
---@return number
function vec3:get(arg0) return nil end

---@param arg0 Object
---@return boolean
function vec3:equals(arg0) return nil end

---@return number
function vec3:length() return nil end

---@param arg0 number
---@return Vec3
function vec3:scale(arg0) return nil end

---@return number
function vec3:x() return nil end

---@param arg0 Vec3
---@return number
function vec3:dot(arg0) return nil end

---@return number
function vec3:z() return nil end

---@return Vec3
function vec3:normalize() return nil end

---@return Vec3
function vec3:reverse() return nil end

---@return number
function vec3:y() return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return Vec3
function vec3:multiply(arg0, arg1, arg2) return nil end

---@param arg0 Vec3
---@return Vec3
function vec3:multiply(arg0) return nil end

---@param arg0 Axes
---@param arg1 number
---@return Vec3
function vec3:with(arg0, arg1) return nil end

---@param arg0 Vec3
---@return Vec3
function vec3:subtract(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return Vec3
function vec3:subtract(arg0, arg1, arg2) return nil end

---@param arg0 Directions
---@param arg1 number
---@return Vec3
function vec3:relative(arg0, arg1) return nil end

---@param arg0 Vec3
---@return number
function vec3:distanceTo(arg0) return nil end

---@return number
function vec3:horizontalDistanceSqr() return nil end

---@param arg0 Vec3
---@return Vec3
function vec3:vectorTo(arg0) return nil end

---@param arg0 Vec3
---@return number
function vec3:distanceToSqr(arg0) return nil end

---@param arg0 number
---@param arg1 number
---@param arg2 number
---@return number
function vec3:distanceToSqr(arg0, arg1, arg2) return nil end

---@param arg0 number
---@return Vec3
function vec3:zRot(arg0) return nil end

---@return number
function vec3:lengthSqr() return nil end

---@param arg0 number
---@return Vec3
function vec3:yRot(arg0) return nil end

---@param arg0 Vec3
---@return Vec3
function vec3:cross(arg0) return nil end

---@param arg0 Position positions can be most things, like a Vec3 or BlockPos
---@param arg1 number
---@return boolean
function vec3:closerThan(arg0, arg1) return nil end

---@param arg0 number
---@return Vec3
function vec3:xRot(arg0) return nil end

---@param arg0 Vec3
---@param arg1 number
---@return Vec3
function vec3:lerp(arg0, arg1) return nil end

---@return number
function vec3:horizontalDistance() return nil end
