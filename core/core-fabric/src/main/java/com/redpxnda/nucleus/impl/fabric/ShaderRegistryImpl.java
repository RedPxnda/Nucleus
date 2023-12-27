package com.redpxnda.nucleus.impl.fabric;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ShaderRegistryImpl {
    public static final List<Triple<Identifier, VertexFormat, Consumer<ShaderProgram>>> SHADERS = new ArrayList<>();

    public static void register(Identifier loc, VertexFormat vertexFormat, Consumer<ShaderProgram> onLoad) {
        SHADERS.add(Triple.of(loc, vertexFormat, onLoad));
    }
}
