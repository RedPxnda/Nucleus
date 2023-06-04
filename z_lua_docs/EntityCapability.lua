---@class EntityCapability
--- EntityCapability represents a custom nucleus system, Capabilities. This is used to store data on entities.
--- Keep in mind, this is just an interface. There is often more methods in implementations.
local entitycapability = {}

---@param arg0 CompoundTag
---@return number
function entitycapability:loadNbt(arg0) return nil end

---@return CompoundTag
function entitycapability:toNbt() return nil end
