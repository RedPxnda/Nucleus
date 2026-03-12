package com.redpxnda.nucleus.trinket.curio;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.trinket.TrinketRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CurioTrinketRenderer implements ICurioRenderer {
    TrinketRenderer renderer;
    public CurioTrinketRenderer(TrinketRenderer renderer){
        this.renderer = renderer;
    }
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext,
            PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        renderer.render(
                stack,
                slotContext.index(),
                matrixStack,
                renderLayerParent.getModel(),
                renderTypeBuffer,
                light,
                limbSwing,
                limbSwingAmount,
                partialTicks,
                ageInTicks,
                netHeadYaw,
                headPitch
                );
    }
}
