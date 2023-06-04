package com.redpxnda.nucleus.impl.fabric;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShaderRegistryImpl {
    public static final List<Triple<ResourceLocation, VertexFormat, Consumer<ShaderInstance>>> SHADERS = new ArrayList<>();

    public static void register(ResourceLocation loc, VertexFormat vertexFormat, Consumer<ShaderInstance> onLoad) {
        SHADERS.add(Triple.of(loc, vertexFormat, onLoad));
    }
}
