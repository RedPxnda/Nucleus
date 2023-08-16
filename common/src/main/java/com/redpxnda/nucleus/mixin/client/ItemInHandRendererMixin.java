package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redpxnda.nucleus.event.RenderEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @WrapOperation(
            method = "renderHandsWithItems",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;evaluateWhichHandsToRender(Lnet/minecraft/client/player/LocalPlayer;)Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;")
    )
    private ItemInHandRenderer.HandRenderSelection nucleus$changeRenderedHandsEvent(LocalPlayer player, Operation<ItemInHandRenderer.HandRenderSelection> original) {
        ItemInHandRenderer.HandRenderSelection selection = original.call(player);
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
        }
        return selection;
    }
}
