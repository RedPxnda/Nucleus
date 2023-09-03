package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public interface BlockPosWrapping {
    static BlockPos getAsBlockPos(BlockPosWrapping wrapping) {
        return (BlockPos) wrapping;
    }

    @WrapperMethod(alias = "center")
    default Vec3 nucleusWrapper$getCenter() {
        return getAsBlockPos(this).getCenter();
    }

    /**
     * Offset this vector 1 unit up
     */
    @WrapperMethod(alias = "above")
    default BlockPos nucleusWrapper$above() {
        return getAsBlockPos(this).above();
    }

    /**
     * Offset this vector 1 unit down
     */
    @WrapperMethod(alias = "below")
    default BlockPos nucleusWrapper$below() {
        return getAsBlockPos(this).below();
    }

    @WrapperMethod(alias = "north")
    default BlockPos nucleusWrapper$north() {
        return getAsBlockPos(this).north();
    }

    @WrapperMethod(alias = "south")
    default BlockPos nucleusWrapper$south() {
        return getAsBlockPos(this).south();
    }

    @WrapperMethod(alias = "west")
    default BlockPos nucleusWrapper$west() {
        return getAsBlockPos(this).west();
    }

    @WrapperMethod(alias = "east")
    default BlockPos nucleusWrapper$east() {
        return getAsBlockPos(this).east();
    }

    @WrapperMethod(alias = "x")
    default int nucleusWrapper$getX() {
        return getAsBlockPos(this).getX();
    }

    @WrapperMethod(alias = "y")
    default int nucleusWrapper$getY() {
        return getAsBlockPos(this).getY();
    }

    @WrapperMethod(alias = "z")
    default int nucleusWrapper$getZ() {
        return getAsBlockPos(this).getZ();
    }
}
