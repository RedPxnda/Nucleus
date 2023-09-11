package com.redpxnda.nucleus.pose;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.capability.entity.EntityDataManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientPoseCapability extends ServerPoseCapability {
    public static final ResourceLocation loc = Nucleus.loc("client_pose");

    public ClientPoseCapability(Entity entity) {
        super(entity);
    }

    public static @Nullable ClientPoseCapability getFor(LivingEntity entity) {
        if (!(entity instanceof Player)) return null;
        return EntityDataManager.getCapability(entity, ClientPoseCapability.class);
    }

    public HumanoidPoseAnimation animation = null;
    public int frameIndex = 0;

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        animation = pose == null || pose.equals("none") ? null : PoseAnimationResourceListener.animations.get(pose);
        frameIndex = 0;
    }
}
