package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.RenderEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "evaluateWhichHandsToRender", at = @At("RETURN"), cancellable = true)
    private static void nucleus$changeRenderingHandsEvent(LocalPlayer player, CallbackInfoReturnable<ItemInHandRenderer.HandRenderSelection> cir) {
        ItemInHandRenderer.HandRenderSelection selection = cir.getReturnValue();
        RenderEvents.RenderedHands hands = switch (selection) {
            case RENDER_BOTH_HANDS -> RenderEvents.RenderedHands.BOTH.copy();
            case RENDER_MAIN_HAND_ONLY -> RenderEvents.RenderedHands.MAINHAND.copy();
            case RENDER_OFF_HAND_ONLY -> RenderEvents.RenderedHands.OFFHAND.copy();
        };
        RenderEvents.CHANGE_RENDERED_HANDS.invoker().evaluate(player, hands);
        if (hands.hasBeenModified()) {
            selection =
                    hands.hasMainhand() && hands.hasOffhand() ? ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS :
                    hands.hasMainhand() ? ItemInHandRenderer.HandRenderSelection.RENDER_MAIN_HAND_ONLY :
                    ItemInHandRenderer.HandRenderSelection.RENDER_OFF_HAND_ONLY;
            cir.setReturnValue(selection);
        }
    }
}
