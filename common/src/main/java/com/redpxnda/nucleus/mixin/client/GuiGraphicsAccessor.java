package com.redpxnda.nucleus.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
    @Accessor
    PoseStack getPose();

    @Accessor
    boolean isManaged();

    @Invoker
    void callFlushIfUnmanaged();

    @Invoker
    void callFlushIfManaged();
}
