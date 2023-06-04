package com.redpxnda.nucleus.impl.forge;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShaderRegistryImpl {
    public static final List<Pair<Function<ResourceProvider, ShaderInstance>, Consumer<ShaderInstance>>> SHADERS = new ArrayList<>();

    public static void register(ResourceLocation loc, VertexFormat vertexFormat, Consumer<ShaderInstance> onLoad) {
        SHADERS.add(Pair.of(rp -> {
            try {
                return new ShaderInstance(rp, loc, vertexFormat);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, onLoad));
    }
}
