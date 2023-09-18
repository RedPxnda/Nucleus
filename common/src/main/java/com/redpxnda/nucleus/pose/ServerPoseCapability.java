package com.redpxnda.nucleus.pose;

import com.redpxnda.nucleus.capability.entity.SyncedEntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.network.clientbound.PoseCapabilitySyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class ServerPoseCapability implements SyncedEntityCapability<NbtCompound> {
    public static ServerPoseCapability getFor(LivingEntity entity) {
        return EntityDataManager.getCapability(entity, ServerPoseCapability.class);
    }

    protected String pose = "none";
    protected Hand usedHand = Hand.MAIN_HAND;
    protected long updateTime = -100;

    public ServerPoseCapability(Entity entity) {
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putString("pose", pose);
        tag.putLong("updateTime", updateTime);
        tag.putString("usedHand", usedHand == Hand.MAIN_HAND ? "main" : "off");
        return tag;
    }

    @Override
    public void loadNbt(NbtCompound tag) {
        pose = tag.getString("pose");
        updateTime = tag.getLong("updateTime");
        usedHand = tag.getString("usedHand").equals("main") ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public void set(String pose, long time) {
        setPose(pose);
        setUpdateTime(time);
    }
    public void set(String pose, long time, Hand usedHand) {
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
    public Hand getUsedHand() {
        return usedHand;
    }
    public void setUsedHand(Hand usedHand) {
        this.usedHand = usedHand;
    }

    @Override
    public PlayerSendable createPacket(Entity target) {
        return new PoseCapabilitySyncPacket(target, this);
    }
}
