---@class Iterable
--- Basically, you can iterate over it. Simple as that.
local iterable = {}

---@return Iterator I ain't documenting all this.
function iterable:iterator() return nil end

---@return Spliterator
function iterable:spliterator() return nil end

---@param arg0 Consumer
---@return number
function iterable:forEach(arg0) return nil end
