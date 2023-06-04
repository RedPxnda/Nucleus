---@class TargetingConditions : 
local targetingconditions = {}

---@param arg0 LivingEntity
---@param arg1 LivingEntity
---@return boolean
function targetingconditions:test(arg0, arg1) return nil end

---@return TargetingConditions
function targetingconditions:copy() return nil end

---@param arg0 number
---@return TargetingConditions
function targetingconditions:range(arg0) return nil end

---@return TargetingConditions
function targetingconditions:ignoreInvisibilityTesting() return nil end

---@return TargetingConditions
function targetingconditions:ignoreLineOfSight() return nil end
