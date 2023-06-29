package com.redpxnda.nucleus.datapack.references.block;

import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import com.redpxnda.nucleus.datapack.references.item.ItemReference;
import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public class BlockReference<B extends Block> extends Reference<B> {
    static { Reference.register(BlockReference.class); }

    public BlockReference(B instance) {
        super(instance);
    }

    // Generated from Block::getName
    public ComponentReference<?> getName() {
        return new ComponentReference<>(instance.getName());
    }

    // Generated from Block::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from Block::getExplosionResistance
    public float getExplosionResistance() {
        return instance.getExplosionResistance();
    }

    // Generated from Block::isPossibleToRespawnInThis
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return instance.isPossibleToRespawnInThis(state);
    }

    // Generated from Block::isRandomlyTicking
    public boolean isRandomlyTicking(BlockStateReference param0) {
        return instance.isRandomlyTicking(param0.instance);
    }

    // Generated from Block::getDescriptionId
    public String getDescriptionId() {
        return instance.getDescriptionId();
    }

    // Generated from Block::getJumpFactor
    public float getJumpFactor() {
        return instance.getJumpFactor();
    }

    // Generated from Block::getSpeedFactor
    public float getSpeedFactor() {
        return instance.getSpeedFactor();
    }

    // Generated from Block::getFriction
    public float getFriction() {
        return instance.getFriction();
    }

    // Generated from Block::asItem
    public ItemReference<?> asItem() {
        return new ItemReference<>(instance.asItem());
    }

    // Generated from Block::hasDynamicShape
    public boolean hasDynamicShape() {
        return instance.hasDynamicShape();
    }

}
