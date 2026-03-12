package com.redpxnda.nucleus.trinket.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.trinket.TrinketRenderer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AccessoriesTrinketRenderer implements AccessoryRenderer {
    TrinketRenderer renderer;

    public AccessoriesTrinketRenderer(TrinketRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public <M extends LivingEntity> void render(
            ItemStack stack,
            SlotReference reference,
            PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        renderer.render(
                stack,
                reference.slot(),
                matrices,
                model,
                multiBufferSource,
                light,
                limbSwing,
                limbSwingAmount,
                partialTicks,
                ageInTicks,
                netHeadYaw,
                headPitch);
    }
}
