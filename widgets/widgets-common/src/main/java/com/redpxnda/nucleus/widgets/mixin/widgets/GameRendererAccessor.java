package com.redpxnda.nucleus.widgets.mixin.widgets;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFov")
    double callHandsOnGetFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting);
}
