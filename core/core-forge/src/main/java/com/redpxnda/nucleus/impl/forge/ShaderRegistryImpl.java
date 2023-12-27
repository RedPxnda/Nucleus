package com.redpxnda.nucleus.impl.forge;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShaderRegistryImpl {
    public static final List<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> SHADERS = new ArrayList<>();

    public static void register(Identifier loc, VertexFormat vertexFormat, Consumer<ShaderProgram> onLoad) {
        SHADERS.add(Pair.of(rp -> {
            try {
                return new ShaderProgram(rp, loc, vertexFormat);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, onLoad));
    }
}
