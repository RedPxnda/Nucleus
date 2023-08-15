package com.redpxnda.nucleus.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public interface RenderEvents {
    Event<EntityRender<LivingEntity, EntityModel<? extends LivingEntity>>> LIVING = EventFactory.createEventResult();
    Event<HudRenderPre> HUD_RENDER_PRE = EventFactory.createEventResult();

    enum Stage {
        /**
         * PRE is called before any rendering is done. Called right at the head of the render method.
         */
        PRE,

        /**
         * PUSHED is called when the {@link PoseStack} is pushed. Use this to make modifications to the {@link PoseStack} provided.
         */
        PUSHED,

        /**
         * SETUP is called after the entity's pose has been set up. Use this to make modifications to any part of the entity's model.
         */
        POSE_SETUP,

        /**
         * POST is called after everything has finished. Called right at the tail of the render method.
         * Returning an event result that interrupts further evaluation does nothing here.
         */
        POST
    }

    interface EntityRender<T extends Entity, M extends EntityModel<? extends T>> {
        /**
         * Fires before an entity is rendered.
         *
         * @param entity            the entity
         * @param entityYaw         i can only guess it's the entity's yaw
         * @param tickDelta         how far the client is between 2 ticks- since you usually have more frames per second than there are ticks per second
         * @return an event result representing whether the rendering should proceed and finish rendering the entity
         */
        EventResult render(Stage stage, M model, T entity, float entityYaw, float tickDelta, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight);
    }

    interface HudRenderPre {
        /**
         * Fires before the player's hud is rendered
         * @return whether the hud should continue rendering
         */
        EventResult render(Minecraft minecraft, GuiGraphics graphics, float partialTick);
    }
}
