package com.redpxnda.nucleus.pose;

import com.redpxnda.nucleus.capability.SyncedEntityCapability;
import com.redpxnda.nucleus.impl.EntityDataManager;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.PoseCapabilitySyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ServerPoseCapability implements SyncedEntityCapability<CompoundTag> {
    public static ServerPoseCapability getFor(LivingEntity entity) {
        return EntityDataManager.getCapability(entity, ServerPoseCapability.class);
    }

    protected String pose = "none";
    protected long updateTime = -100;

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("pose", pose);
        tag.putLong("updateTime", updateTime);
        return tag;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        pose = tag.getString("pose");
        updateTime = tag.getLong("updateTime");
    }

    public void setPose(String pose) {
        this.pose = pose;
    }
    public String getPose() {
        return pose;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public SimplePacket createPacket(Entity target) {
        return new PoseCapabilitySyncPacket(target, this);
    }
}
