package com.redpxnda.nucleus.impl;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ShaderRegistry {
    @ExpectPlatform
    public static void register(ResourceLocation loc, VertexFormat vertexFormat, Consumer<ShaderInstance> onLoad) {
        throw new AssertionError();
    }
}
