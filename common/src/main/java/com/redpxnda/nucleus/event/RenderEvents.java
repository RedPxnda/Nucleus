package com.redpxnda.nucleus.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public interface RenderEvents {
    PrioritizedEvent<EntityRender<LivingEntity, EntityModel<? extends LivingEntity>>> LIVING = PrioritizedEvent.createEventResult();
    PrioritizedEvent<HudRenderPre> HUD_RENDER_PRE = PrioritizedEvent.createEventResult();
    PrioritizedEvent<ChangeRenderedHands> CHANGE_RENDERED_HANDS = PrioritizedEvent.createCustomHandler((map, args) -> {
        Player player = args.get(0);
        RenderedHands hands = args.get(1);
        for (ChangeRenderedHands listener : map.keySet()) {
            listener.evaluate(player, hands);
            if (hands.isForced()) break;
        }
        return null;
    });

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

    interface ChangeRenderedHands {
        /**
         * Fires whenever minecraft attempts to evaluate what hands should be rendered.
         * NOTE: enabling offhand rendering will not magically make the offhand render.
         * Minecraft never actually renders the offhand, and has additional checks
         * to make sure of that. This will only make a difference when the player
         * has an offhand item, and their mainhand item prevents the rendering of the
         * offhand item. (eg. crossbows hide offhand item when charged)
         * If you want to actually render the offhand, there are other events where
         * you can do that.
         * @param player the player being checked
         * @param hands  the hands minecraft chose to render - use this object to make any modifications.
         */
        void evaluate(Player player, RenderedHands hands);
    }
    class RenderedHands {
        public static final RenderedHands BOTH = new RenderedHands(true, true);
        public static final RenderedHands OFFHAND = new RenderedHands(true, false);
        public static final RenderedHands MAINHAND = new RenderedHands(false, true);

        private boolean forced = false;
        private boolean offhand;
        private boolean mainhand;
        private boolean hasBeenModified = false;

        protected RenderedHands(boolean offhand, boolean mainhand) {
            this.offhand = offhand;
            this.mainhand = mainhand;
        }
        protected RenderedHands(RenderedHands other) {
            this.forced = other.forced;
            this.offhand = other.offhand;
            this.mainhand = other.mainhand;
            this.hasBeenModified = other.hasBeenModified;
        }

        public RenderedHands copy() {
            return new RenderedHands(this);
        }

        public boolean hasBeenModified() {
            return hasBeenModified;
        }

        public void setOffhand(boolean bl) {
            offhand = bl;
            hasBeenModified = true;
        }
        public void setMainhand(boolean bl) {
            mainhand = bl;
            hasBeenModified = true;
        }

        public boolean hasOffhand() {
            return offhand;
        }
        public boolean hasMainhand() {
            return mainhand;
        }

        public void force() {
            forced = true;
        }
        public boolean isForced() {
            return forced;
        }
    }
}
