package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface LevelWrapping {
    static Level getAsLevel(LevelWrapping wrapping) {
        return (Level) wrapping;
    }
    
    @WrapperMethod(alias = "is_client_side")
    default boolean nucleusWrapper$isClientSide() {
        return getAsLevel(this).isClientSide();
    }

    @Nullable
    @WrapperMethod(alias = "server")
    default MinecraftServer nucleusWrapper$getServer() {
        return getAsLevel(this).getServer();
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4. Always returns true on the client because vanilla has no need for it on the client, therefore it is not synced to the client
     */
    @WrapperMethod(alias = "is_day")
    default boolean nucleusWrapper$isDay() {
        return getAsLevel(this).isDay();
    }

    @WrapperMethod(alias = "is_night")
    default boolean nucleusWrapper$isNight() {
        return getAsLevel(this).isNight();
    }

    @WrapperMethod(alias = "shared_spawn_pos")
    default BlockPos nucleusWrapper$getSharedSpawnPos() {
        return getAsLevel(this).getSharedSpawnPos();
    }

    @WrapperMethod(alias = "shared_spawn_angle")
    default float nucleusWrapper$getSharedSpawnAngle() {
        return getAsLevel(this).getSharedSpawnAngle();
    }

    @WrapperMethod(alias = "sea_level")
    default int nucleusWrapper$getSeaLevel() {
        return getAsLevel(this).getSeaLevel();
    }

    @WrapperMethod(alias = "game_time")
    default long nucleusWrapper$getGameTime() {
        return getAsLevel(this).getGameTime();
    }

    @WrapperMethod(alias = "day_time")
    default long nucleusWrapper$getDayTime() {
        return getAsLevel(this).getDayTime();
    }

    /**
     * Returns {@code true} if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    @WrapperMethod(alias = "is_thundering")
    default boolean nucleusWrapper$isThundering() {
        return getAsLevel(this).isThundering();
    }

    /**
     * Returns {@code true} if the current rain strength is greater than 0.2
     */
    @WrapperMethod(alias = "is_raining")
    default boolean nucleusWrapper$isRaining() {
        return getAsLevel(this).isRaining();
    }

    @WrapperMethod(alias = "sky_darken")
    default int nucleusWrapper$getSkyDarken() {
        return getAsLevel(this).getSkyDarken();
    }

    @WrapperMethod(alias = "difficulty")
    default int nucleusWrapper$getDifficulty() {
        return getAsLevel(this).getDifficulty().getId();
    }

    @WrapperMethod(alias = { "difficulty_string", "difficulty_str" })
    default String nucleusWrapper$getDifficultyString() {
        return getAsLevel(this).getDifficulty().getKey();
    }

    @WrapperMethod(alias = "max_light_level")
    default int nucleusWrapper$getMaxLightLevel() {
        return getAsLevel(this).getMaxLightLevel();
    }

    @WrapperMethod(alias = "moon_brightness")
    default float nucleusWrapper$getMoonBrightness() {
        return getAsLevel(this).getMoonBrightness();
    }

    @WrapperMethod(alias = "moon_phase")
    default int nucleusWrapper$getMoonPhase() {
        return getAsLevel(this).getMoonPhase();
    }

    @WrapperMethod(alias = "min_build_height")
    default int nucleusWrapper$getMinBuildHeight() {
        return getAsLevel(this).getMinBuildHeight();
    }

    @WrapperMethod(alias = "height")
    default int nucleusWrapper$getHeight() {
        return getAsLevel(this).getHeight();
    }

    @WrapperMethod(alias = "max_build_height")
    default int nucleusWrapper$getMaxBuildHeight() {
        return getAsLevel(this).getMaxBuildHeight();
    }
}
