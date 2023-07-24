package com.redpxnda.nucleus.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public interface RenderEvents {
    Event<EntityRenderPre<LivingEntity>> LIVING_PRE = EventFactory.createEventResult();
    /**
     * LIVING_PUSHED fires after the matrix stack is pushed, allowing modifications to the current matrix.
     */
    Event<EntityRenderPre<LivingEntity>> LIVING_PUSHED = EventFactory.createEventResult();
    Event<EntityRenderPost<LivingEntity>> LIVING_POST = EventFactory.createLoop();
    Event<HudRenderPre> HUD_RENDER_PRE = EventFactory.createEventResult();

    interface EntityRenderPre<T extends Entity> {
        /**
         * Fires before an entity is rendered.
         *
         * @param entity            the entity
         * @param entityYaw         i can only guess it's the entity's yaw
         * @param partialTick       how far the client is between 2 ticks- since you usually have more frames per second than there are ticks
         * @return an event result representing whether the rendering should proceed and finish rendering the entity
         */
        EventResult render(T entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight);
    }
    interface EntityRenderPost<T extends Entity> {
        /**
         * Fires after an entity is rendered.
         */
        void render(T entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight);
    }
    interface HudRenderPre {
        /**
         * Fires before the player's hud is rendered
         * @return whether the hud should continue rendering
         */
        EventResult render(Minecraft minecraft, GuiGraphics graphics, float partialTick);
    }
}
