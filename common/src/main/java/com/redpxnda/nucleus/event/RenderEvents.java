package com.redpxnda.nucleus.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.client.ArmRenderer;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface RenderEvents {
    PrioritizedEvent<EntityRender<LivingEntity, EntityModel<? extends LivingEntity>>> LIVING_ENTITY_RENDER = PrioritizedEvent.createEventResult();
    PrioritizedEvent<HudRenderPre> HUD_RENDER_PRE = PrioritizedEvent.createEventResult();
    PrioritizedEvent<ChangeRenderedHands> CHANGE_RENDERED_HANDS = PrioritizedEvent.of(listeners -> (player, hands) -> {
        for (ChangeRenderedHands listener : listeners.keySet()) {
            listener.evaluate(player, hands);
            if (hands.isForced()) break;
        }
    });
    PrioritizedEvent<RenderArmWithItem> RENDER_ARM_WITH_ITEM = PrioritizedEvent.createEventResult();

    enum EntityRenderStage {
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
         * @param partialTick       how far the client is between 2 ticks- since you usually have more frames per second than there are ticks per second
         * @return an event result representing whether the rendering should proceed and finish rendering the entity
         */
        EventResult render(EntityRenderStage stage, M model, T entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight);
    }

    interface HudRenderPre {
        /**
         * Fires before the player's hud is rendered
         * @return whether the hud should continue rendering
         */
        EventResult render(Minecraft minecraft, GuiGraphics graphics, float partialTick);
    }

    /**
     * All stages are skipped if the player is scoping with a spyglass.
     */
    enum ArmRenderStage {
        /**
         * PUSHED is the earliest stage, being called when the matrix stack is initially pushed.
         * Use this stage if you want to completely overwrite arm rendering, perhaps for a custom item or something.
         */
        PUSHED(true),

        /**
         * ARM is called when only the player's arm is being rendered. (AKA when the player's held itemstack is empty)
         * <p></p>
         * NOTE: vanilla arm (when no item is held) rendering has two conditions:
         * 1. the hand in question is the main hand.
         * 2. the player is not invisible.
         * This event fires before both conditions. Re-implement them yourself.
         */
        ARM(true),

        /**
         * ITEM is called when minecraft attempts to render the item in the player's hand, as long as it is some generic item.
         * Eg. Loaded crossbows and maps will not pass into this stage since they have custom renders.
         * Use this stage when you want to move rendered items based on conditions.
         */
        ITEM(true);

        final boolean canBeInterrupted;

        ArmRenderStage(boolean canBeInterrupted) {
            this.canBeInterrupted = canBeInterrupted;
        }

        public boolean shouldInterrupt() {
            return canBeInterrupted;
        }
    }

    interface RenderArmWithItem {
        /**
         * Fires when a player's hand is being rendered.
         * NOTE: this will fire twice if both the offhand and the mainhand are being rendered,
         * since they are done separately. Use the ChangeRenderedHands event to control that.
         * @return an interrupting result if you want to cancel vanilla rendering (as well as
         * other listeners)
         */
        EventResult render(ArmRenderStage stage, ArmRenderer armRenderer, AbstractClientPlayer player, PoseStack matrices,
                           MultiBufferSource buffer, ItemStack stack, InteractionHand hand,
                           float partialTicks, float pitch, float swingProgress,
                           float equippedProgress, int combinedLight);
    }

    interface ChangeRenderedHands {
        /**
         * Fires whenever minecraft attempts to evaluate what hands should be rendered.
         * NOTE: enabling offhand rendering will not magically make the offhand render.
         * Minecraft never actually renders the offhand, and has additional checks
         * to make sure of that. This will only make a difference when the player
         * has an item in their offhand, or if you disable offhand rendering.
         * Additionally, disabling both the offhand and mainhand rendering won't do
         * anything. It will just act as if only the offhand should be rendering.
         * If you want to actually render the offhand, there are other events where
         * you can do that.
         * @param player the player being checked
         * @param hands  the hands minecraft chose to render - use this object to make any modifications.
         */
        void evaluate(LocalPlayer player, RenderedHands hands);
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
