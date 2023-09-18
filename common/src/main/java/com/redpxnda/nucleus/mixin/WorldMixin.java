package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.resolving.wrappers.WorldWrapping;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public class WorldMixin implements WorldWrapping {
}
