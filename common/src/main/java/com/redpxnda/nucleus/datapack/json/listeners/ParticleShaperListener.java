package com.redpxnda.nucleus.datapack.json.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.datapack.json.JsonParticleShaping;
import com.redpxnda.nucleus.util.ParticleShaper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class ParticleShaperListener extends SimpleJsonResourceReloadListener {
    private static final Gson gson = new Gson();
    public static final Map<ResourceLocation, ParticleShaper> shapers = new HashMap<>();

    public ParticleShaperListener() {
        super(gson, "particle_shaping");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        object.forEach((rl, json) -> {
            ParticleShaper shaper =
                    JsonParticleShaping.completeCodec.parse(JsonOps.INSTANCE, json).resultOrPartial(
                            s -> JsonParticleShaping.LOGGER.error("Failed to parse particle shaper {}: {}", rl, s))
                            .orElse(new JsonParticleShaping.Circle(ParticleTypes.ANGRY_VILLAGER, 1, 2).setup());
            shapers.put(rl, shaper);
        });
    }
}
