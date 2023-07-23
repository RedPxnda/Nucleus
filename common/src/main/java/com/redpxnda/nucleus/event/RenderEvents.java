package com.redpxnda.nucleus.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface RenderEvents {
    Event<PreRender<LivingEntity>> LIVING_PRE = EventFactory.createEventResult();
    Event<PostRender<LivingEntity>> LIVING_POST = EventFactory.createLoop();

    interface PreRender<T extends Entity> {
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
    interface PostRender<T extends Entity> {
        /**
         * Fires after an entity is rendered.
         */
        void render(T entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight);
    }
}
