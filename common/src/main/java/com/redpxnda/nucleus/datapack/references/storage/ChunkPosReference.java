package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import net.minecraft.util.math.ChunkPos;

@SuppressWarnings("unused")
public class ChunkPosReference extends Reference<ChunkPos> {
    static { Reference.register(ChunkPosReference.class); }

    public ChunkPosReference(ChunkPos instance) {
        super(instance);
    }

    // Generated from ChunkPos::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from ChunkPos::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from ChunkPos::getMiddleBlockPosition
    public BlockPosReference getMiddleBlockPosition(int param0) {
        return new BlockPosReference(instance.getCenterAtY(param0));
    }

    // Generated from ChunkPos::getChessboardDistance
    public int getChessboardDistance(ChunkPosReference param0) {
        return instance.getChebyshevDistance(param0.instance);
    }

    // Generated from ChunkPos::getMiddleBlockX
    public int getMiddleBlockX() {
        return instance.getCenterX();
    }

    // Generated from ChunkPos::getBlockZ
    public int getBlockZ(int param0) {
        return instance.getOffsetZ(param0);
    }

    // Generated from ChunkPos::getMaxBlockX
    public int getMaxBlockX() {
        return instance.getEndX();
    }

    // Generated from ChunkPos::getMaxBlockZ
    public int getMaxBlockZ() {
        return instance.getEndZ();
    }

    // Generated from ChunkPos::getRegionX
    public int getRegionX() {
        return instance.getRegionX();
    }

    // Generated from ChunkPos::getMinBlockZ
    public int getMinBlockZ() {
        return instance.getStartZ();
    }

    // Generated from ChunkPos::getMinBlockX
    public int getMinBlockX() {
        return instance.getStartX();
    }

    // Generated from ChunkPos::getRegionZ
    public int getRegionZ() {
        return instance.getRegionZ();
    }

    // Generated from ChunkPos::toLong
    public long toLong() {
        return instance.toLong();
    }

    // Generated from ChunkPos::getBlockX
    public int getBlockX(int param0) {
        return instance.getOffsetX(param0);
    }

    // Generated from ChunkPos::getMiddleBlockZ
    public int getMiddleBlockZ() {
        return instance.getCenterZ();
    }

    // Generated from ChunkPos::getRegionLocalX
    public int getRegionLocalX() {
        return instance.getRegionRelativeX();
    }

    // Generated from ChunkPos::getBlockAt
    public BlockPosReference getBlockAt(int param0, int param1, int param2) {
        return new BlockPosReference(instance.getBlockPos(param0, param1, param2));
    }

    // Generated from ChunkPos::getRegionLocalZ
    public int getRegionLocalZ() {
        return instance.getRegionRelativeZ();
    }

    // Generated from ChunkPos::getWorldPosition
    public BlockPosReference getWorldPosition() {
        return new BlockPosReference(instance.getStartPos());
    }
}
