package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.json.JsonParticleShaping;
import com.redpxnda.nucleus.datapack.json.listeners.ParticleShaperListener;
import com.redpxnda.nucleus.math.ParticleShaper;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class SyncParticleShapersPacket extends SyncJsonMapPacket {
    public SyncParticleShapersPacket(Map<String, JsonElement> elements) {
        super(elements);
    }
    public SyncParticleShapersPacket() {
        super(ParticleShaperListener.toSync);
    }

    public SyncParticleShapersPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ParticleShaperListener.shapers.clear();
        elements.forEach((rl, json) -> {
            ParticleShaper shaper =
                    JsonParticleShaping.completeCodec.parse(JsonOps.INSTANCE, json).getOrThrow(
                            false,
                            s -> Nucleus.LOGGER.error("Failed to parse particle shaper {} -> {}", rl, s));
            ParticleShaperListener.shapers.put(new ResourceLocation(rl), shaper);
        });
    }
}
