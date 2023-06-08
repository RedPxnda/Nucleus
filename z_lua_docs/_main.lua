---@class Reference
--- Reference is the base class for well, everything.
--- Why references? Because Minecraft classes are automatically obfuscated, so intermediate classes need to be created to circumvent this.

---@class Object
--- Object is the base class for everything that isn't a reference. (Either things I didn't setup References for, or things that don't get obfuscated.)

---@class Optional
--- Optional represents exactly that: something that is Optional. This is java's Optional class. (Eg. Optional<Player> would mean that it could be a player or it could be nothing)

---@class Enum
--- Enums represent sets of statically defined constants.

---@class Class
--- Represents a Class object.

---@alias jList List
--- Self explanatory. The java list.
---@see List

---@alias jSet Set
---@see Set

---@alias jIterable Iterable
---@see Iterable

---@alias jCollection Collection
---@see Collection

---@alias jMap Map
--- The java Map. Maps a value to every key.
--- Example: { someKey=someValue, someCoolerKey=epicValue, bestKey=kingOfKeys }
---@see Map

---@alias jUUID UUID
--- The java UUID. Just an Identifier used for a bunch of things.
---@see UUID

---@alias nStatics Statics
--- Nucleus's general utility method holder. Usually available for most lua scripts.
---@see Statics