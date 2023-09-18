package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.resolving.wrappers.BlockPosWrapping;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPos.class)
public class BlockPosMixin implements BlockPosWrapping {
}
