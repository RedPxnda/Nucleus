package com.redpxnda.nucleus.pose.client;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientPoseFacet extends ServerPoseFacet {
    public static FacetKey<ClientPoseFacet> KEY;
    public static final Identifier loc = Nucleus.loc("entity_pose_client");

    public ClientPoseFacet(Entity entity) {
        super(entity);
    }

    public static @Nullable ClientPoseFacet get(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) return null;
        return KEY.get(entity);
    }

    public HumanoidPoseAnimation animation = null;
    public int frameIndex = 0;

    @Override
    public void loadNbt(NbtCompound tag) {
        super.loadNbt(tag);
        animation = pose == null || pose.equals("none") ? null : PoseAnimationResourceListener.animations.get(pose);
        frameIndex = 0;
    }
}
