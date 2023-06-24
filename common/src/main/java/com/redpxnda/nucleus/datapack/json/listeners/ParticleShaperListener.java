package com.redpxnda.nucleus.datapack.json.listeners;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.json.JsonParticleShaping;
import com.redpxnda.nucleus.math.ParticleShaper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

import static com.redpxnda.nucleus.Nucleus.GSON;

public class ParticleShaperListener extends SimpleJsonResourceReloadListener {
    public static final Map<ResourceLocation, ParticleShaper> shapers = new HashMap<>();
    public static final Map<String, JsonElement> toSync = new HashMap<>();

    public ParticleShaperListener() {
        super(GSON, "particle_shaping");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        shapers.clear();
        object.forEach((rl, json) -> {
            ParticleShaper shaper =
                    JsonParticleShaping.completeCodec.parse(JsonOps.INSTANCE, json).getOrThrow(
                            false,
                            s -> Nucleus.LOGGER.error("Failed to parse particle shaper {} -> {}", rl, s));
            if (shaper instanceof JsonParticleShaping.StoringParticleShaper sps && sps.syncToClient)
                toSync.put(rl.toString(), json);
            shapers.put(rl, shaper);
        });
    }
}
