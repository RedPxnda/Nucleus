package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.resolving.wrappers.LevelWrapping;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class LevelMixin implements LevelWrapping {
}
