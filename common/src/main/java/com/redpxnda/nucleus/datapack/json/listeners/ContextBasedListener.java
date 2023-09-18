package com.redpxnda.nucleus.datapack.json.listeners;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.json.passive.ContextHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import static com.redpxnda.nucleus.Nucleus.GSON;

public class ContextBasedListener<T extends ContextHoldingHandler> extends JsonDataLoader {
    protected final Codec<T> codec;
    protected final List<T> deserialized = new ArrayList<>();

    public ContextBasedListener(String string, Codec<T> codec) {
        super(GSON, string);
        this.codec = codec;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> object, ResourceManager resourceManager, Profiler profilerFiller) {
        object.forEach((loc, json) -> {
            Either<T, DataResult.PartialResult<T>> result = codec.parse(JsonOps.INSTANCE, json).get();
            result.ifRight(partial -> Nucleus.LOGGER.error("Error whilst parsing JSON at " + loc + ".\nMessage:\n" + partial.message()));
            result.ifLeft(left -> {
                left.putContext(new ContextHolder(json.getAsJsonObject().get("context").getAsJsonObject()));
                deserialized.add(left);
            });
        });
    }
}
