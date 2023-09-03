package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.resolving.wrappers.EntityWrapping;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements EntityWrapping {
}
