package com.redpxnda.nucleus.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

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
        put("shifting", Entity::isShiftKeyDown);
        put("sprinting", Entity::isSprinting);
        put("swimming", Entity::isSwimming);
        put("crouching", Entity::isCrouching);
        put("freezing", Entity::isFreezing);
        put("invisible", Entity::isInvisible);
        put("is_on_fire", Entity::isOnFire);
        put("spectator", Entity::isSpectator);
        put("silent", Entity::isSilent);
        put("is_in_lava", Entity::isInLava);
        put("is_in_water", Entity::isInWater);
        put("is_in_wall", Entity::isInWall);
        put("is_on_ground", Entity::onGround);
        put("is_on_portal_cooldown", Entity::isOnPortalCooldown);
        put("is_under_water", Entity::isUnderWater);
        put("is_vehicle", Entity::isVehicle);
        put("is_passenger", Entity::isPassenger);
        put("on_x", e -> e.getOnPos().getX());
        put("on_y", e -> e.getOnPos().getY());
        put("on_z", e -> e.getOnPos().getZ());
        put("name", Entity::getScoreboardName);
        put("uuid", e -> e.getUUID().toString());
        put("id", Entity::getId);
        put("frozen_percent", Entity::getPercentFrozen);
    }});
    public static final StatManager<LivingEntity> livingEntity = new StatManager<LivingEntity>(entity).with(new HashMap<>(){{
        put("health", LivingEntity::getHealth);
        put("max_health", LivingEntity::getMaxHealth);
        put("armor", e -> e.getAttributeValue(Attributes.ARMOR));
        put("armor_toughness", e -> e.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        put("arrows", LivingEntity::getArrowCount);
        put("speed", LivingEntity::getSpeed);
        put("movement_speed", e -> e.getAttributeValue(Attributes.MOVEMENT_SPEED));
        put("xp_reward", LivingEntity::getExperienceReward);
    }});
    public static final StatManager<Player> player = new StatManager<Player>(livingEntity).with(new HashMap<>(){{
        put("xp", PlayerUtil::getTotalXp);
        put("xp_level", p -> p.experienceLevel);
        put("is_creative", Player::isCreative);
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
