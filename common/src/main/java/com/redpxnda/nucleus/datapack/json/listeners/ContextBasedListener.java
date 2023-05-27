package com.redpxnda.nucleus.datapack.json.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.datapack.json.passive.ContextHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextBasedListener<T extends ContextHoldingHandler> extends SimpleJsonResourceReloadListener {
    protected static Gson GSON = new Gson();
    private static Logger LOGGER = LogUtils.getLogger();

    protected final Codec<T> codec;
    protected final List<T> deserialized = new ArrayList<>();

    public ContextBasedListener(String string, Codec<T> codec) {
        super(GSON, string);
        this.codec = codec;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        object.forEach((loc, json) -> {
            Either<T, DataResult.PartialResult<T>> result = codec.parse(JsonOps.INSTANCE, json).get();
            result.ifRight(partial -> LOGGER.error("Error whilst parsing JSON at " + loc + ".\nMessage:\n" + partial.message()));
            result.ifLeft(left -> {
                left.putContext(new ContextHolder(json.getAsJsonObject().get("context").getAsJsonObject()));
                deserialized.add(left);
            });
        });
    }
}
