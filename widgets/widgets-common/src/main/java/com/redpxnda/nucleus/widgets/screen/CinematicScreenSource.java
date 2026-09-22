package com.redpxnda.nucleus.widgets.screen;

import com.redpxnda.nucleus.math.Transform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public interface CinematicScreenSource {

    Transform getLookingTransform(BlockPos pos, BlockState state);

    MovingCinematicScreen.CinematicCameraLimits getCamLimits(BlockPos targetBlock, BlockState targetBlockState);
}
