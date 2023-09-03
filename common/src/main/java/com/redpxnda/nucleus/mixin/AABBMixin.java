package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.resolving.wrappers.AABBWrapping;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AABB.class)
public class AABBMixin implements AABBWrapping {
}
