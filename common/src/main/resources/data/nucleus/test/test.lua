local func = function(item)
    return item:getCount() > 1
end

function test_coolFunc(player)
    print(player:isHolding(Statics:itemOf("minecraft:stick")))
    print(player:isHolding(func))
end