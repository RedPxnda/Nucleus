package com.redpxnda.nucleus.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class PlayerUtil {
    public static int getTotalXpForLevel(int level) {
        if (level < 17) return level * level + 6 * level;
        if (level < 32) return MathHelper.floor(2.5 * level * level - 40.5 * level + 360);
        return MathHelper.floor(4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getTotalXp(PlayerEntity player) {
        return MathHelper.floor(player.experienceProgress * player.getNextLevelExperience() + getTotalXpForLevel(player.experienceLevel));
    }
}
