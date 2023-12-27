package com.redpxnda.nucleus.mixin.client;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Accessor
    boolean isRunningDrawCallback();

    @Invoker
    void callTryDraw();

    @Invoker
    void callDrawIfRunning();
}
