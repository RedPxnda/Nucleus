package com.redpxnda.nucleus.pose.server;

import com.redpxnda.nucleus.facet.entity.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.pose.network.clientbound.PoseFacetSyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class ServerPoseFacet implements EntityFacet<NbtCompound> {
    public static FacetKey<ServerPoseFacet> KEY;

    public static ServerPoseFacet get(ServerPlayerEntity entity) {
        return KEY.get(entity);
    }

    public String pose = "none";
    public Hand usedHand = Hand.MAIN_HAND;
    public long updateTime = -100;

    public ServerPoseFacet(Entity entity) {
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
    public void set(String pose, ServerPlayerEntity facetHolder) {
        set(pose, facetHolder.getWorld().getTime());
        sendToTrackers(facetHolder);
        sendToClient(facetHolder);
    }
    public void set(String pose, ServerPlayerEntity facetHolder, Hand usedHand) {
        set(pose, facetHolder.getWorld().getTime(), usedHand);
        sendToTrackers(facetHolder);
        sendToClient(facetHolder);
    }
    public void reset() {
        setPose("none");
    }
    public void reset(long time) {
        setPose("none");
        setUpdateTime(time);
    }
    public void reset(ServerPlayerEntity facetHolder) {
        set("none", facetHolder.getWorld().getTime());
        sendToTrackers(facetHolder);
        sendToClient(facetHolder);
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
        return new PoseFacetSyncPacket(target, this);
    }
}
