package com.redpxnda.nucleus.datapack.references.block;

import com.redpxnda.nucleus.datapack.references.*;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.Vec3Reference;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public class BlockStateReference extends Reference<BlockState> {
    static { Reference.register(BlockStateReference.class); }

    public BlockStateReference(BlockState instance) {
        super(instance);
    }

    // Generated from BlockStateBase::is
    public boolean is(BlockReference<?> param0) {
        return instance.is(param0.instance);
    }

//    // Generated from BlockStateBase::is
//    public boolean is(TagKey param0) {
//        return instance.is(param0);
//    }

    // Generated from BlockStateBase::getOffset
    public Vec3Reference getOffset(LevelReference param0, BlockPosReference param1) {
        return new Vec3Reference(instance.getOffset(param0.instance, param1.instance));
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
        return instance.getSeed(param0.instance);
    }

    // Generated from BlockStateBase::hasAnalogOutputSignal
    public boolean hasAnalogOutputSignal() {
        return instance.hasAnalogOutputSignal();
    }

    // Generated from BlockStateBase::isRedstoneConductor
    public boolean isRedstoneConductor(LevelReference param0, BlockPosReference param1) {
        return instance.isRedstoneConductor(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getAnalogOutputSignal
    public int getAnalogOutputSignal(LevelReference param0, BlockPosReference param1) {
        return instance.getAnalogOutputSignal(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::requiresCorrectToolForDrops
    public boolean requiresCorrectToolForDrops() {
        return instance.requiresCorrectToolForDrops();
    }

    // Generated from BlockStateBase::propagatesSkylightDown
    public boolean propagatesSkylightDown(LevelReference param0, BlockPosReference param1) {
        return instance.propagatesSkylightDown(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::useShapeForLightOcclusion
    public boolean useShapeForLightOcclusion() {
        return instance.useShapeForLightOcclusion();
    }

    // Generated from BlockStateBase::entityCanStandOnFace
    public boolean entityCanStandOnFace(LevelReference param0, BlockPosReference param1, EntityReference<?> param2, Statics.Directions param3) {
        return instance.entityCanStandOnFace(param0.instance, param1.instance, param2.instance, param3.instance);
    }

    // Generated from BlockStateBase::hasLargeCollisionShape
    public boolean hasLargeCollisionShape() {
        return instance.hasLargeCollisionShape();
    }

    // Generated from BlockStateBase::isCollisionShapeFullBlock
    public boolean isCollisionShapeFullBlock(LevelReference param0, BlockPosReference param1) {
        return instance.isCollisionShapeFullBlock(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::isRandomlyTicking
    public boolean isRandomlyTicking() {
        return instance.isRandomlyTicking();
    }

    // Generated from BlockStateBase::hasBlockEntity
    public boolean hasBlockEntity() {
        return instance.hasBlockEntity();
    }

    // Generated from BlockStateBase::isFaceSturdy
    public boolean isFaceSturdy(LevelReference param0, BlockPosReference param1, Statics.Directions param2) {
        return instance.isFaceSturdy(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::isAir
    public boolean isAir() {
        return instance.isAir();
    }

    // Generated from BlockStateBase::canOcclude
    public boolean canOcclude() {
        return instance.canOcclude();
    }

    // Generated from BlockStateBase::isSuffocating
    public boolean isSuffocating(LevelReference param0, BlockPosReference param1) {
        return instance.isSuffocating(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::isViewBlocking
    public boolean isViewBlocking(LevelReference param0, BlockPosReference param1) {
        return instance.isViewBlocking(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getLightBlock
    public int getLightBlock(LevelReference param0, BlockPosReference param1) {
        return instance.getLightBlock(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getBlock
    public BlockReference<?> getBlock() {
        return new BlockReference<>(instance.getBlock());
    }

    // Generated from BlockStateBase::getShadeBrightness
    public float getShadeBrightness(LevelReference param0, BlockPosReference param1) {
        return instance.getShadeBrightness(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::isSignalSource
    public boolean isSignalSource() {
        return instance.isSignalSource();
    }

    // Generated from BlockStateBase::getLightEmission
    public int getLightEmission() {
        return instance.getLightEmission();
    }

    // Generated from BlockStateBase::getDestroySpeed
    public float getDestroySpeed(LevelReference param0, BlockPosReference param1) {
        return instance.getDestroySpeed(param0.instance, param1.instance);
    }

    // Generated from BlockStateBase::getSignal
    public int getSignal(LevelReference param0, BlockPosReference param1, Statics.Directions param2) {
        return instance.getSignal(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::getDestroyProgress
    public float getDestroyProgress(PlayerReference param0, LevelReference param1, BlockPosReference param2) {
        return instance.getDestroyProgress(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::getDirectSignal
    public int getDirectSignal(LevelReference param0, BlockPosReference param1, Statics.Directions param2) {
        return instance.getDirectSignal(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::triggerEvent
    public boolean triggerEvent(LevelReference param0, BlockPosReference param1, int param2, int param3) {
        return instance.triggerEvent(param0.instance, param1.instance, param2, param3);
    }

    // Generated from BlockStateBase::entityCanStandOn
    public boolean entityCanStandOn(LevelReference param0, BlockPosReference param1, EntityReference<?> param2) {
        return instance.entityCanStandOn(param0.instance, param1.instance, param2.instance);
    }

    // Generated from BlockStateBase::attack
    public void attack(LevelReference param0, BlockPosReference param1, PlayerReference param2) {
        instance.attack(param0.instance, param1.instance, param2.instance);
    }
}
