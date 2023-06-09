package com.redpxnda.nucleus.datapack.references.block;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.Statics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;

@SuppressWarnings("unused")
public class BlockPosReference extends Reference<BlockPos> implements Position {
    static { Reference.register(BlockPosReference.class); }

    public BlockPosReference(BlockPos instance) {
        super(instance);
    }

    // Generated from BlockPos::offset
    public BlockPosReference offset(int param0, int param1, int param2) {
        return new BlockPosReference(instance.offset(param0, param1, param2));
    }

    // Generated from BlockPos::offset
    public BlockPosReference offset(double param0, double param1, double param2) {
        return new BlockPosReference(instance.offset(param0, param1, param2));
    }

    // Generated from BlockPos::multiply
    public BlockPosReference multiply(int param0) {
        return new BlockPosReference(instance.multiply(param0));
    }

    // Generated from BlockPos::rotate
    public BlockPosReference rotate(Statics.Rotations param0) {
        return new BlockPosReference(instance.rotate(param0.instance));
    }

    // Generated from BlockPos::relative
    public BlockPosReference relative(Statics.Directions param0) {
        return new BlockPosReference(instance.relative(param0.instance));
    }

    // Generated from BlockPos::relative
    public BlockPosReference relative(Statics.Axes param0, int param1) {
        return new BlockPosReference(instance.relative(param0.instance, param1));
    }

    // Generated from BlockPos::relative
    public BlockPosReference relative(Statics.Directions param0, int param1) {
        return new BlockPosReference(instance.relative(param0.instance, param1));
    }

    // Generated from BlockPos::immutable
    public BlockPosReference immutable() {
        return new BlockPosReference(instance.immutable());
    }

    // Generated from BlockPos::below
    public BlockPosReference below() {
        return new BlockPosReference(instance.below());
    }

    // Generated from BlockPos::below
    public BlockPosReference below(int param0) {
        return new BlockPosReference(instance.below(param0));
    }

    // Generated from BlockPos::west
    public BlockPosReference west(int param0) {
        return new BlockPosReference(instance.west(param0));
    }

    // Generated from BlockPos::west
    public BlockPosReference west() {
        return new BlockPosReference(instance.west());
    }

    // Generated from BlockPos::east
    public BlockPosReference east(int param0) {
        return new BlockPosReference(instance.east(param0));
    }

    // Generated from BlockPos::east
    public BlockPosReference east() {
        return new BlockPosReference(instance.east());
    }

    // Generated from BlockPos::south
    public BlockPosReference south() {
        return new BlockPosReference(instance.south());
    }

    // Generated from BlockPos::south
    public BlockPosReference south(int param0) {
        return new BlockPosReference(instance.south(param0));
    }

    // Generated from BlockPos::north
    public BlockPosReference north(int param0) {
        return new BlockPosReference(instance.north(param0));
    }

    // Generated from BlockPos::north
    public BlockPosReference north() {
        return new BlockPosReference(instance.north());
    }

    // Generated from BlockPos::above
    public BlockPosReference above() {
        return new BlockPosReference(instance.above());
    }

    // Generated from BlockPos::above
    public BlockPosReference above(int param0) {
        return new BlockPosReference(instance.above(param0));
    }

    // Generated from BlockPos::atY
    public BlockPosReference atY(int param0) {
        return new BlockPosReference(instance.atY(param0));
    }

    // Generated from BlockPos::asLong
    public long asLong() {
        return instance.asLong();
    }

    @Override
    public double x() {
        return instance.getX();
    }

    @Override
    public double y() {
        return instance.getY();
    }

    @Override
    public double z() {
        return instance.getZ();
    }
}
