function test_coolFunc(player)
    local level = player:getLevel()
    local tnt = Statics.Entities:createTNT(level, player:getX(), player:getY(), player:getZ(), player, 60)

    local xRot = player:getXRot()
    local yRot = player:getYRot()

    local xAmp = 0
    local mult = 1.5
    --local zAmp = 0.25

    local x = -math.sin(yRot * (math.pi / 180)) * math.cos(xRot * (math.pi / 180))
    local y = -math.sin((xRot + xAmp) * (math.pi / 180))
    local z = math.cos(yRot * (math.pi / 180)) * math.cos(xRot * (math.pi / 180))

    local vector = Statics:createVec3(x, y, z):normalize():scale(mult)

    tnt:setDeltaMovement(vector:x(), vector:y(), vector:z())
    level:addFreshEntity(tnt)
end