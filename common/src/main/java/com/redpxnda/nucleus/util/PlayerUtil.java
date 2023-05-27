package com.redpxnda.nucleus.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PlayerUtil {
    public static int getTotalXpForLevel(int level) {
        if (level < 17) return level * level + 6 * level;
        if (level < 32) return Mth.floor(2.5 * level * level - 40.5 * level + 360);
        return Mth.floor(4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getTotalXp(Player player) {
        return Mth.floor(player.experienceProgress * player.getXpNeededForNextLevel() + getTotalXpForLevel(player.experienceLevel));
    }
}
