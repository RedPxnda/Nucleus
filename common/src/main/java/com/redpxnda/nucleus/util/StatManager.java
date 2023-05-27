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
        put("blockX", Entity::getBlockX);
        put("blockY", Entity::getBlockY);
        put("blockZ", Entity::getBlockZ);
        put("shifting", Entity::isShiftKeyDown);
        put("sprinting", Entity::isSprinting);
        put("swimming", Entity::isSwimming);
        put("crouching", Entity::isCrouching);
        put("freezing", Entity::isFreezing);
        put("invisible", Entity::isInvisible);
        put("onFire", Entity::isOnFire);
        put("spectator", Entity::isSpectator);
        put("silent", Entity::isSilent);
        put("inLava", Entity::isInLava);
        put("inWater", Entity::isInWater);
        put("inWall", Entity::isInWall);
        put("onGround", Entity::isOnGround);
        put("onPortalCooldown", Entity::isOnPortalCooldown);
        put("underWater", Entity::isUnderWater);
        put("vehicle", Entity::isVehicle);
        put("passenger", Entity::isPassenger);
        put("onX", e -> e.getOnPos().getX());
        put("onY", e -> e.getOnPos().getY());
        put("onZ", e -> e.getOnPos().getZ());
        put("name", Entity::getScoreboardName);
        put("uuid", e -> e.getUUID().toString());
        put("id", Entity::getId);
        put("frozenPercent", Entity::getPercentFrozen);
    }});
    public static final StatManager<LivingEntity> livingEntity = new StatManager<LivingEntity>(entity).with(new HashMap<>(){{
        put("health", LivingEntity::getHealth);
        put("maxHealth", LivingEntity::getMaxHealth);
        put("armor", e -> e.getAttributeValue(Attributes.ARMOR));
        put("armorToughness", e -> e.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        put("arrows", LivingEntity::getArrowCount);
        put("speed", LivingEntity::getSpeed);
        put("movementSpeed", e -> e.getAttributeValue(Attributes.MOVEMENT_SPEED));
        put("experienceReward", LivingEntity::getExperienceReward);
    }});
    public static final StatManager<Player> player = new StatManager<Player>(livingEntity).with(new HashMap<>(){{
        put("experience", PlayerUtil::getTotalXp);
        put("level", p -> p.experienceLevel);
        put("creative", Player::isCreative);
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
