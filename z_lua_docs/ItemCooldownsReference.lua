---@class ItemCooldowns : 
local itemcooldowns = {}

---@param arg0 Item
---@param arg1 number
---@return number
function itemcooldowns:getCooldownPercent(arg0, arg1) return nil end

---@param arg0 Item
---@return boolean
function itemcooldowns:isOnCooldown(arg0) return nil end

---@param arg0 Item
---@return void
function itemcooldowns:removeCooldown(arg0) return nil end

---@param arg0 Item
---@param arg1 number
---@return void
function itemcooldowns:addCooldown(arg0, arg1) return nil end
