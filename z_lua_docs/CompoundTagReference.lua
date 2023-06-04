---@class CompoundTag : Tag
local compoundtag = {}

---@param arg0 string
---@return void
function compoundtag:remove(arg0) return nil end

---@param arg0 string
---@return Tag
function compoundtag:get(arg0) return nil end

---@param arg0 string
---@param arg1 Tag
---@return void
function compoundtag:put(arg0, arg1) return nil end

---@param arg0 Object
---@return boolean
function compoundtag:equals(arg0) return nil end

---@return string
function compoundtag:toString() return nil end

---@param arg0 string
---@return boolean
function compoundtag:getBoolean(arg0) return nil end

---@param arg0 string
---@param arg1 boolean
---@return void
function compoundtag:putBoolean(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getByte(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return void
function compoundtag:putByte(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getShort(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return void
function compoundtag:putShort(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getInt(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return void
function compoundtag:putInt(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getLong(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return void
function compoundtag:putLong(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getFloat(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return void
function compoundtag:putFloat(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getDouble(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return void
function compoundtag:putDouble(arg0, arg1) return nil end

---@return boolean
function compoundtag:isEmpty() return nil end

---@return number
function compoundtag:size() return nil end

---@param arg0 string
---@return boolean
function compoundtag:contains(arg0) return nil end

---@param arg0 string
---@param arg1 number
---@return boolean
function compoundtag:contains(arg0, arg1) return nil end

---@param arg0 CompoundTag
---@return CompoundTag
function compoundtag:merge(arg0) return nil end

---@return number
function compoundtag:getId() return nil end

---@return Tag
function compoundtag:copy() return nil end

---@return CompoundTag
function compoundtag:copy() return nil end

---@param arg0 string
---@param arg1 number[]
---@return void
function compoundtag:putByteArray(arg0, arg1) return nil end

---@param arg0 string
---@param arg1 List
---@return void
function compoundtag:putByteArray(arg0, arg1) return nil end

---@param arg0 string
---@return string
function compoundtag:getString(arg0) return nil end

---@param arg0 string
---@return UUID
function compoundtag:getUUID(arg0) return nil end

---@param arg0 string
---@return number[]
function compoundtag:getIntArray(arg0) return nil end

---@param arg0 string
---@return boolean
function compoundtag:hasUUID(arg0) return nil end

---@param arg0 string
---@param arg1 UUID
---@return void
function compoundtag:putUUID(arg0, arg1) return nil end

---@param arg0 string
---@param arg1 string
---@return void
function compoundtag:putString(arg0, arg1) return nil end

---@param arg0 string
---@param arg1 number
---@return ListTag
function compoundtag:getList(arg0, arg1) return nil end

---@param arg0 string
---@return number
function compoundtag:getTagType(arg0) return nil end

---@param arg0 string
---@return number[]
function compoundtag:getByteArray(arg0) return nil end

---@param arg0 string
---@return CompoundTag
function compoundtag:getCompound(arg0) return nil end

---@return Set
function compoundtag:getAllKeys() return nil end

---@param arg0 string
---@param arg1 List
---@return void
function compoundtag:putIntArray(arg0, arg1) return nil end

---@param arg0 string
---@param arg1 number[]
---@return void
function compoundtag:putIntArray(arg0, arg1) return nil end

---@param arg0 string
---@return number[]
function compoundtag:getLongArray(arg0) return nil end

---@param arg0 string
---@param arg1 number[]
---@return void
function compoundtag:putLongArray(arg0, arg1) return nil end

---@param arg0 string
---@param arg1 List
---@return void
function compoundtag:putLongArray(arg0, arg1) return nil end
