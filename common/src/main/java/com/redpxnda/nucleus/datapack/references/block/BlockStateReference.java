package com.redpxnda.nucleus.datapack.references.block;

import com.redpxnda.nucleus.datapack.references.*;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.Vec3Reference;
import net.minecraft.block.BlockState;

@SuppressWarnings("unused")
public class BlockStateReference extends Reference<BlockState> {
    static { Reference.register(BlockStateReference.class); }

    public BlockStateReference(BlockState instance) {
        super(instance);
    }

    // Generated from BlockStateBase::is
    public boolean is(BlockReference<?> param0) {
        return instance.isOf(param0.instance);
    }

//    // Generated from BlockStateBase::is
//    public boolean is(TagKey param0) {
//        return instance.is(param0);
//    }

    // Generated from BlockStateBase::getOffset
    public Vec3Reference getOffset(LevelReference param0, BlockPosReference param1) {
        return new Vec3Reference(instance.getModelOffset(param0.instance, param1.instance));
    }

    // Generated from BlockStateBase::rotate
    public BlockStateReference rotate(Statics.Rotations param0) {
        return new BlockStateReference(instance.rotate(param0.instance));
    }

//    // Generated from BlockStateBase::use
//    public void use(LevelReference param0, PlayerReference param1, Enums.InteractionHands param2, BlockHitResult param3) {
//        return instance.use(param0.instance, param1.instance, param2.instance, param3);
//    }

    // Generated from BlockStateBase::getSeed
    public long getSeed(BlockPosReference param0) {
        return instance.getRenderingSeed(param0.instance);
    }

    // Generated from BlockStateBase::hasAnalogOutputSignal
    public boolean hasAnalogOutputSignal() {
        return instance.hasComparatorOutput();
    }

    // Generated from BlockStateBase::isRedstoneConductor
    public boolean isRedstoneConductor(LevelReference param0, BlockPosReference param1) {
        return instance.isSolidBlock(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getAnalogOutputSignal
    public int getAnalogOutputSignal(LevelReference param0, BlockPosReference param1) {
        return instance.getComparatorOutput(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::requiresCorrectToolForDrops
    public boolean requiresCorrectToolForDrops() {
        return instance.isToolRequired();
    }

    // Generated from BlockStateBase::propagatesSkylightDown
    public boolean propagatesSkylightDown(LevelReference param0, BlockPosReference param1) {
        return instance.isTransparent(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::useShapeForLightOcclusion
    public boolean useShapeForLightOcclusion() {
        return instance.hasSidedTransparency();
    }

    // Generated from BlockStateBase::entityCanStandOnFace
    public boolean entityCanStandOnFace(LevelReference param0, BlockPosReference param1, EntityReference<?> param2, Statics.Directions param3) {
        return instance.isSolidSurface(param0.instance, param1.instance, param2.instance, param3.instance);
    }

    // Generated from BlockStateBase::hasLargeCollisionShape
    public boolean hasLargeCollisionShape() {
        return instance.exceedsCube();
    }

    // Generated from BlockStateBase::isCollisionShapeFullBlock
    public boolean isCollisionShapeFullBlock(LevelReference param0, BlockPosReference param1) {
        return instance.isFullCube(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::isRandomlyTicking
    public boolean isRandomlyTicking() {
        return instance.hasRandomTicks();
    }

    // Generated from BlockStateBase::hasBlockEntity
    public boolean hasBlockEntity() {
        return instance.hasBlockEntity();
    }

    // Generated from BlockStateBase::isFaceSturdy
    public boolean isFaceSturdy(LevelReference param0, BlockPosReference param1, Statics.Directions param2) {
        return instance.isSideSolidFullSquare(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::isAir
    public boolean isAir() {
        return instance.isAir();
    }

    // Generated from BlockStateBase::canOcclude
    public boolean canOcclude() {
        return instance.isOpaque();
    }

    // Generated from BlockStateBase::isSuffocating
    public boolean isSuffocating(LevelReference param0, BlockPosReference param1) {
        return instance.shouldSuffocate(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::isViewBlocking
    public boolean isViewBlocking(LevelReference param0, BlockPosReference param1) {
        return instance.shouldBlockVision(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getLightBlock
    public int getLightBlock(LevelReference param0, BlockPosReference param1) {
        return instance.getOpacity(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getBlock
    public BlockReference<?> getBlock() {
        return new BlockReference<>(instance.getBlock());
    }

    // Generated from BlockStateBase::getShadeBrightness
    public float getShadeBrightness(LevelReference param0, BlockPosReference param1) {
        return instance.getAmbientOcclusionLightLevel(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::isSignalSource
    public boolean isSignalSource() {
        return instance.emitsRedstonePower();
    }

    // Generated from BlockStateBase::getLightEmission
    public int getLightEmission() {
        return instance.getLuminance();
    }

    // Generated from BlockStateBase::getDestroySpeed
    public float getDestroySpeed(LevelReference param0, BlockPosReference param1) {
        return instance.getHardness(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getSignal
    public int getSignal(LevelReference param0, BlockPosReference param1, Statics.Directions param2) {
        return instance.getWeakRedstonePower(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::getDestroyProgress
    public float getDestroyProgress(PlayerReference param0, LevelReference param1, BlockPosReference param2) {
        return instance.calcBlockBreakingDelta(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::getDirectSignal
    public int getDirectSignal(LevelReference param0, BlockPosReference param1, Statics.Directions param2) {
        return instance.getStrongRedstonePower(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::triggerEvent
    public boolean triggerEvent(LevelReference param0, BlockPosReference param1, int param2, int param3) {
        return instance.onSyncedBlockEvent(param0.instance, param1.instance, param2, param3);
    }

    // Generated from BlockStateBase::entityCanStandOn
    public boolean entityCanStandOn(LevelReference param0, BlockPosReference param1, EntityReference<?> param2) {
        return instance.hasSolidTopSurface(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::attack
    public void attack(LevelReference param0, BlockPosReference param1, PlayerReference param2) {
        instance.onBlockBreakStart(param0.instance, param1.instance, param2.instance);
    }
}
