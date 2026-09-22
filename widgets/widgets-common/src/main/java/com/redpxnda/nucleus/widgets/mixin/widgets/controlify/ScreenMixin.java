package com.redpxnda.nucleus.widgets.mixin.widgets.controlify;

import com.redpxnda.nucleus.widgets.controlify.NucleusScreenProcessor;
import com.redpxnda.nucleus.widgets.controller.ControllerScreen;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Screen.class)
public abstract class ScreenMixin implements ScreenProcessorProvider {
    @Unique
    private ScreenProcessor<?> nucleus$processor;

    @Override
    public ScreenProcessor<?> screenProcessor() {
        if (!(this instanceof ControllerScreen screen))
            return null;

        if (nucleus$processor == null)
            nucleus$processor = new NucleusScreenProcessor(screen);

        return nucleus$processor;
    }
}