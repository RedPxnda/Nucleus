package com.redpxnda.nucleus.pose.client;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientPoseFacet extends ServerPoseFacet {
    public static FacetKey<ClientPoseFacet> KEY;
    public static final ResourceLocation loc = Nucleus.loc("entity_pose_client");

    public ClientPoseFacet(Entity entity) {
        super(entity);
    }

    public static @Nullable ClientPoseFacet get(LivingEntity entity) {
        if (!(entity instanceof Player)) return null;
        return KEY.get(entity);
    }

    public HumanoidPoseAnimation animation = null;
    public int frameIndex = 0;
    public boolean reset;

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        animation = pose == null || pose.equals("none") ? null : PoseAnimationResourceListener.animations.get(pose);
        if (animation == null) {
            reset = true;
        }
        frameIndex = 0;
    }
}
