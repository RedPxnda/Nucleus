package com.redpxnda.nucleus.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ShaderRegistry {
    @ExpectPlatform
    public static void register(Identifier loc, VertexFormat vertexFormat, Consumer<ShaderProgram> onLoad) {
        throw new AssertionError();
    }
}
