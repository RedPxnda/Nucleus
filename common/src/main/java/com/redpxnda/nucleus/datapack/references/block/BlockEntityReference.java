package com.redpxnda.nucleus.datapack.references.block;

import com.redpxnda.nucleus.datapack.references.*;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("unused")
public class BlockEntityReference<E extends BlockEntity> extends Reference<E> {
    static { Reference.register(BlockEntityReference.class); }

    public BlockEntityReference(E instance) {
        super(instance);
    }

    // Generated from BlockEntity::getLevel
    public LevelReference getLevel() {
        return new LevelReference(instance.getLevel());
    }

    // Generated from BlockEntity::setLevel
    public void setLevel(LevelReference param0) {
        instance.setLevel(param0.instance);
    }

    // Generated from BlockEntity::hasLevel
    public boolean hasLevel() {
        return instance.hasLevel();
    }

    // Generated from BlockEntity::saveWithId
    public CompoundTagReference saveWithId() {
        return new CompoundTagReference(instance.saveWithId());
    }

    // Generated from BlockEntity::saveToItem
    public void saveToItem(ItemStackReference param0) {
        instance.saveToItem(param0.instance);
    }

/*    // Generated from BlockEntity::setBlockState
    public void setBlockState(BlockStateReference param0) {
        instance.setBlockState(param0.instance);
    }*/

    // Generated from BlockEntity::setRemoved
    public void setRemoved() {
        instance.setRemoved();
    }

    // Generated from BlockEntity::isRemoved
    public boolean isRemoved() {
        return instance.isRemoved();
    }

    // Generated from BlockEntity::getUpdateTag
    public CompoundTagReference getUpdateTag() {
        return new CompoundTagReference(instance.getUpdateTag());
    }

    // Generated from BlockEntity::getBlockPos
    public BlockPosReference getBlockPos() {
        return new BlockPosReference(instance.getBlockPos());
    }

    // Generated from BlockEntity::getBlockState
    public BlockStateReference getBlockState() {
        return new BlockStateReference(instance.getBlockState());
    }

    // Generated from BlockEntity::setChanged
    public void setChanged() {
        instance.setChanged();
    }

    // Generated from BlockEntity::saveWithFullMetadata
    public CompoundTagReference saveWithFullMetadata() {
        return new CompoundTagReference(instance.saveWithFullMetadata());
    }

    // Generated from BlockEntity::saveWithoutMetadata
    public CompoundTagReference saveWithoutMetadata() {
        return new CompoundTagReference(instance.saveWithoutMetadata());
    }
}
