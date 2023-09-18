package com.redpxnda.nucleus.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatManager<E> {
    public static final StatManager<Entity> entity = new StatManager<>(new HashMap<>(){{
        put("x", Entity::getX);
        put("y", Entity::getY);
        put("z", Entity::getZ);
        put("block_x", Entity::getBlockX);
        put("block_y", Entity::getBlockY);
        put("block_z", Entity::getBlockZ);
        put("shifting", Entity::isSneaking);
        put("sprinting", Entity::isSprinting);
        put("swimming", Entity::isSwimming);
        put("crouching", Entity::isInSneakingPose);
        put("freezing", Entity::shouldEscapePowderSnow);
        put("invisible", Entity::isInvisible);
        put("is_on_fire", Entity::isOnFire);
        put("spectator", Entity::isSpectator);
        put("silent", Entity::isSilent);
        put("is_in_lava", Entity::isInLava);
        put("is_in_water", Entity::isTouchingWater);
        put("is_in_wall", Entity::isInsideWall);
        put("is_on_ground", Entity::isOnGround);
        put("is_on_portal_cooldown", Entity::hasPortalCooldown);
        put("is_under_water", Entity::isSubmergedInWater);
        put("is_vehicle", Entity::hasPassengers);
        put("is_passenger", Entity::hasVehicle);
        put("on_x", e -> e.getSteppingPos().getX());
        put("on_y", e -> e.getSteppingPos().getY());
        put("on_z", e -> e.getSteppingPos().getZ());
        put("name", Entity::getEntityName);
        put("uuid", e -> e.getUuid().toString());
        put("id", Entity::getId);
        put("frozen_percent", Entity::getFreezingScale);
    }});
    public static final StatManager<LivingEntity> livingEntity = new StatManager<LivingEntity>(entity).with(new HashMap<>(){{
        put("health", LivingEntity::getHealth);
        put("max_health", LivingEntity::getMaxHealth);
        put("armor", e -> e.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
        put("armor_toughness", e -> e.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        put("arrows", LivingEntity::getStuckArrowCount);
        put("speed", LivingEntity::getMovementSpeed);
        put("movement_speed", e -> e.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        put("xp_reward", LivingEntity::getXpToDrop);
    }});
    public static final StatManager<PlayerEntity> player = new StatManager<PlayerEntity>(livingEntity).with(new HashMap<>(){{
        put("xp", PlayerUtil::getTotalXp);
        put("xp_level", p -> p.experienceLevel);
        put("is_creative", PlayerEntity::isCreative);
    }});

    private final Map<String, Function<? super E, ?>> functions;

    public StatManager(Map<String, Function<? super E, ?>> functions) {
        this.functions = functions;
    }
    public StatManager(StatManager<? super E> other) {
        this.functions = new HashMap<>(other.functions);
    }

    public StatManager<E> with(Map<String, Function<? super E, ?>> map) {
        this.functions.putAll(map);
        return this;
    }

    public Map<String, Function<? super E, ?>> functions() {
        return functions;
    }

    public Map<String, Object> evaluate(E entity) {
        return functions.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue().apply(entity)))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }
}
