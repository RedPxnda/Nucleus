package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.resolving.wrappers.BoxWrapping;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Box.class)
public class BoxMixin implements BoxWrapping {
}
