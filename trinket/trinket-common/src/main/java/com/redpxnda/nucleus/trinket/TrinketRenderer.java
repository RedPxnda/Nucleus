package com.redpxnda.nucleus.trinket;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface TrinketRenderer {
    void render(ItemStack stack, int slotIndex,
                PoseStack matrixStack, EntityModel<? extends LivingEntity> model,
                MultiBufferSource bufferSource,
                int light, float limbSwing, float limbSwingAmount,
                float tickDelta, float animProgress, float netHeadYaw,
                float headPitch);
}
