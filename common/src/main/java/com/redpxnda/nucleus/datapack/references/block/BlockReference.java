package com.redpxnda.nucleus.datapack.references.block;

import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import com.redpxnda.nucleus.datapack.references.item.ItemReference;
import com.redpxnda.nucleus.datapack.references.Reference;

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
        return instance.getBlastResistance();
    }

    // Generated from Block::isPossibleToRespawnInThis
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return instance.canMobSpawnInside(state);
    }

    // Generated from Block::isRandomlyTicking
    public boolean isRandomlyTicking(BlockStateReference param0) {
        return instance.hasRandomTicks(param0.instance);
    }

    // Generated from Block::getDescriptionId
    public String getDescriptionId() {
        return instance.getTranslationKey();
    }

    // Generated from Block::getJumpFactor
    public float getJumpFactor() {
        return instance.getJumpVelocityMultiplier();
    }

    // Generated from Block::getSpeedFactor
    public float getSpeedFactor() {
        return instance.getVelocityMultiplier();
    }

    // Generated from Block::getFriction
    public float getFriction() {
        return instance.getSlipperiness();
    }

    // Generated from Block::asItem
    public ItemReference<?> asItem() {
        return new ItemReference<>(instance.asItem());
    }

    // Generated from Block::hasDynamicShape
    public boolean hasDynamicShape() {
        return instance.hasDynamicBounds();
    }

}
