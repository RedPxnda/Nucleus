package com.redpxnda.nucleus.pose;

import com.redpxnda.nucleus.capability.entity.SyncedEntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.network.clientbound.PoseCapabilitySyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ServerPoseCapability implements SyncedEntityCapability<CompoundTag> {
    public static ServerPoseCapability getFor(LivingEntity entity) {
        return EntityDataManager.getCapability(entity, ServerPoseCapability.class);
    }

    protected String pose = "none";
    protected InteractionHand usedHand = InteractionHand.MAIN_HAND;
    protected long updateTime = -100;

    public ServerPoseCapability(Entity entity) {
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("pose", pose);
        tag.putLong("updateTime", updateTime);
        tag.putString("usedHand", usedHand == InteractionHand.MAIN_HAND ? "main" : "off");
        return tag;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        pose = tag.getString("pose");
        updateTime = tag.getLong("updateTime");
        usedHand = tag.getString("usedHand").equals("main") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public void set(String pose, long time) {
        setPose(pose);
        setUpdateTime(time);
    }
    public void set(String pose, long time, InteractionHand usedHand) {
        set(pose, time);
        setUsedHand(usedHand);
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
    public InteractionHand getUsedHand() {
        return usedHand;
    }
    public void setUsedHand(InteractionHand usedHand) {
        this.usedHand = usedHand;
    }

    @Override
    public PlayerSendable createPacket(Entity target) {
        return new PoseCapabilitySyncPacket(target, this);
    }
}
