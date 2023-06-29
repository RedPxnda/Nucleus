package com.redpxnda.nucleus.datapack.references;

import com.redpxnda.nucleus.datapack.references.block.BlockEntityReference;
import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.block.BlockReference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.AABBReference;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import com.redpxnda.nucleus.util.MiscUtil;
import jdk.jfr.Label;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.luaj.vm2.LuaFunction;

import java.util.List;

@SuppressWarnings("unused")
public class LevelReference extends Reference<Level> {
    static { Reference.register(LevelReference.class); }

    public LevelReference(Level instance) {
        super(instance);
    }

    public boolean addFreshEntity(EntityReference<?> entity) {
        return instance.addFreshEntity(entity.instance);
    }

    // Generated from Level::isLoaded
    public boolean isLoaded(BlockPosReference param0) {
        return instance.isLoaded(param0.instance);
    }

    // Generated from Level::loadedAndEntityCanStandOn
    public boolean loadedAndEntityCanStandOn(BlockPosReference param0, EntityReference<?> param1) {
        return instance.loadedAndEntityCanStandOn(param0.instance, param1.instance);
    }

    // Generated from Level::getBestNeighborSignal
    public int getBestNeighborSignal(BlockPosReference param0) {
        return instance.getBestNeighborSignal(param0.instance);
    }

    // Generated from Level::destroyBlockProgress
    public void destroyBlockProgress(int param0, BlockPosReference param1, int param2) {
        instance.destroyBlockProgress(param0, param1.instance, param2);
    }

    // Generated from Level::loadedAndEntityCanStandOnFace
    public boolean loadedAndEntityCanStandOnFace(BlockPosReference param0, EntityReference<?> param1, Statics.Directions param2) {
        return instance.loadedAndEntityCanStandOnFace(param0.instance, param1.instance, param2.instance);
    }

    // Generated from Level::getSharedSpawnAngle
    public float getSharedSpawnAngle() {
        return instance.getSharedSpawnAngle();
    }

    // Generated from Level::neighborShapeChanged
    public void neighborShapeChanged(Statics.Directions param0, BlockStateReference param1, BlockPosReference param2, BlockPosReference param3, int param4, int param5) {
        instance.neighborShapeChanged(param0.instance, param1.instance, param2.instance, param3.instance, param4, param5);
    }

/*    // Generated from Level::getServer
    public MinecraftServerReference getServer() {
        return new MinecraftServerReference(instance.getServer());
   }*/

    // Generated from Level::isInWorldBounds
    public boolean isInWorldBounds(BlockPosReference param0) {
        return instance.isInWorldBounds(param0.instance);
    }

    // Generated from Level::setBlock
    public boolean setBlock(BlockPosReference param0, BlockStateReference param1, int param2) {
        return instance.setBlock(param0.instance, param1.instance, param2);
    }

    // Generated from Level::setBlock
    public boolean setBlock(BlockPosReference param0, BlockStateReference param1, int param2, int param3) {
        return instance.setBlock(param0.instance, param1.instance, param2, param3);
    }

    // Generated from Level::getBlockState
    public BlockStateReference getBlockState(BlockPosReference param0) {
        return new BlockStateReference(instance.getBlockState(param0.instance));
    }

    // Generated from Level::getSeaLevel
    public int getSeaLevel() {
        return instance.getSeaLevel();
    }

    // Generated from Level::setBlockAndUpdate
    public boolean setBlockAndUpdate(BlockPosReference param0, BlockStateReference param1) {
        return instance.setBlockAndUpdate(param0.instance, param1.instance);
    }

    // Generated from Level::updateNeighborsAt
    public void updateNeighborsAt(BlockPosReference param0, BlockReference<?> param1) {
        instance.updateNeighborsAt(param0.instance, param1.instance);
    }

    // Generated from Level::destroyBlock
    public boolean destroyBlock(BlockPosReference param0, boolean param1, EntityReference<?> param2, int param3) {
        return instance.destroyBlock(param0.instance, param1, param2.instance, param3);
    }

    // Generated from Level::neighborChanged
    public void neighborChanged(BlockStateReference param0, BlockPosReference param1, BlockReference<?> param2, BlockPosReference param3, boolean param4) {
        instance.neighborChanged(param0.instance, param1.instance, param2.instance, param3.instance, param4);
    }

    // Generated from Level::neighborChanged
    public void neighborChanged(BlockPosReference param0, BlockReference<?> param1, BlockPosReference param2) {
        instance.neighborChanged(param0.instance, param1.instance, param2.instance);
    }

    // Generated from Level::getBlockEntity
    public BlockEntityReference<?> getBlockEntity(BlockPosReference param0) {
        return new BlockEntityReference<>(instance.getBlockEntity(param0.instance));
    }

    // Generated from Level::removeBlock
    public boolean removeBlock(BlockPosReference param0, boolean param1) {
        return instance.removeBlock(param0.instance, param1);
    }

    // Generated from Level::sendBlockUpdated
    public void sendBlockUpdated(BlockPosReference param0, BlockStateReference param1, BlockStateReference param2, int param3) {
        instance.sendBlockUpdated(param0.instance, param1.instance, param2.instance, param3);
    }

    // Generated from Level::isNight
    public boolean isNight() {
        return instance.isNight();
    }

    // Generated from Level::isDay
    public boolean isDay() {
        return instance.isDay();
    }

    // Generated from Level::getSunAngle
    public float getSunAngle(float param0) {
        return instance.getSunAngle(param0);
    }

    // Generated from Level::shouldTickBlocksAt
    public boolean shouldTickBlocksAt(BlockPosReference param0) {
        return instance.shouldTickBlocksAt(param0.instance);
    }

    // Generated from Level::shouldTickBlocksAt
    public boolean shouldTickBlocksAt(long param0) {
        return instance.shouldTickBlocksAt(param0);
    }

    public void explodeByMob(EntityReference<?> param0, double param1, double param2, double param3, float param4, boolean makeFire) {
        instance.explode(param0.instance, param1, param2, param3, param4, makeFire, Level.ExplosionInteraction.MOB);
    }

    public void explodeByTnt(EntityReference<?> param0, double param1, double param2, double param3, float param4, boolean makeFire) {
        instance.explode(param0.instance, param1, param2, param3, param4, makeFire, Level.ExplosionInteraction.TNT);
    }

    public void explodeByBlock(EntityReference<?> param0, double param1, double param2, double param3, float param4, boolean makeFire) {
        instance.explode(param0.instance, param1, param2, param3, param4, makeFire, Level.ExplosionInteraction.BLOCK);
    }

    public void explodeByNone(EntityReference<?> param0, double param1, double param2, double param3, float param4, boolean makeFire) {
        instance.explode(param0.instance, param1, param2, param3, param4, makeFire, Level.ExplosionInteraction.NONE);
    }

    // Generated from Level::getThunderLevel
    public float getThunderLevel(float param0) {
        return instance.getThunderLevel(param0);
    }

    // Generated from Level::getRainLevel
    public float getRainLevel(float param0) {
        return instance.getRainLevel(param0);
    }

    // Generated from Level::setSpawnSettings
    @Label("Unsafe")
    private void setSpawnSettings(boolean param0, boolean param1) {
        instance.setSpawnSettings(param0, param1);
    }

    // Generated from Level::isRaining
    public boolean isRaining() {
        return instance.isRaining();
    }

    // Generated from Level::getSharedSpawnPos
    public BlockPosReference getSharedSpawnPos() {
        return new BlockPosReference(instance.getSharedSpawnPos());
    }

/*    // Generated from Level::getWorldBorder
    public WorldBorder getWorldBorder() {
        return instance.getWorldBorder();
    }*/

    // Generated from Level::isThundering
    public boolean isThundering() {
        return instance.isThundering();
    }

    // Generated from Level::setBlockEntity
    public void setBlockEntity(BlockEntityReference<?> param0) {
        instance.setBlockEntity(param0.instance);
    }

    // Generated from Level::removeBlockEntity
    public void removeBlockEntity(BlockPosReference param0) {
        instance.removeBlockEntity(param0.instance);
    }

    // Generated from Level::getDirectSignalTo
    public int getDirectSignalTo(BlockPosReference param0) {
        return instance.getDirectSignalTo(param0.instance);
    }

    // Generated from Level::blockEntityChanged
    public void blockEntityChanged(BlockPosReference param0) {
        instance.blockEntityChanged(param0.instance);
    }

    // Generated from Level::getEntity
    public EntityReference<?> getEntity(int param0) {
        return new EntityReference<>(instance.getEntity(param0));
    }

    // Generated and modified from Level::getEntities
    public List<EntityReference<Entity>> getEntities(EntityReference<?> param0, AABBReference param1, LuaFunction param2) {
        return instance.getEntities(param0.instance, param1.instance, MiscUtil.mapPredicate(MiscUtil.predicateFromFunc(EntityReference.class, param2), EntityReference::new))
                .stream()
                .map(EntityReference::new)
                .toList();
    }

    // Generated from Level::hasSignal
    public boolean hasSignal(BlockPosReference param0, Statics.Directions param1) {
        return instance.hasSignal(param0.instance, param1.instance);
    }

    // Generated from Level::getSignal
    public int getSignal(BlockPosReference param0, Statics.Directions param1) {
        return instance.getSignal(param0.instance, param1.instance);
    }

/*    // Generated from Level::getGameRules
    public GameRules getGameRules() {
        return instance.getGameRules();
    }*/

    // Generated from Level::setRainLevel
    public void setRainLevel(float param0) {
        instance.setRainLevel(param0);
    }

    // Generated from Level::isRainingAt
    public boolean isRainingAt(BlockPosReference param0) {
        return instance.isRainingAt(param0.instance);
    }

    // Generated from Level::hasNeighborSignal
    public boolean hasNeighborSignal(BlockPosReference param0) {
        return instance.hasNeighborSignal(param0.instance);
    }

    // Generated from Level::mayInteract
    public boolean mayInteract(PlayerReference param0, BlockPosReference param1) {
        return instance.mayInteract(param0.instance, param1.instance);
    }

    // Generated from Level::getGameTime
    public long getGameTime() {
        return instance.getGameTime();
    }

    // Generated from Level::setThunderLevel
    public void setThunderLevel(float param0) {
        instance.setThunderLevel(param0);
    }

    // Generated from Level::getDayTime
    public long getDayTime() {
        return instance.getDayTime();
    }

    // Generated from Level::createFireworks
    public void createFireworks(double param0, double param1, double param2, double param3, double param4, double param5, CompoundTagReference param6) {
        instance.createFireworks(param0, param1, param2, param3, param4, param5, param6.instance);
    }

    // Generated from Level::getFreeMapId
    public int getFreeMapId() {
        return instance.getFreeMapId();
    }

    // Generated from Level::isStateAtPosition
    public boolean isStateAtPosition(BlockPosReference param0, LuaFunction param1) {
        return instance.isStateAtPosition(param0.instance, MiscUtil.mapPredicate(MiscUtil.predicateFromFunc(BlockStateReference.class, param1), BlockStateReference::new));
    }

    // Generated from Level::setSkyFlashTime
    public void setSkyFlashTime(int param0) {
        instance.setSkyFlashTime(param0);
    }

    // Generated from Level::getSkyDarken
    public int getSkyDarken() {
        return instance.getSkyDarken();
    }

    // Generated from Level::getBlockRandomPos
    public BlockPosReference getBlockRandomPos(int param0, int param1, int param2, int param3) {
        return new BlockPosReference(instance.getBlockRandomPos(param0, param1, param2, param3));
    }
}
