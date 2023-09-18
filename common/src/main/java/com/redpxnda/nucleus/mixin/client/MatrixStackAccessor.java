package com.redpxnda.nucleus.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(MatrixStack.class)
public interface MatrixStackAccessor {
    @Accessor
    Deque<MatrixStack.Entry> getStack();
}
