package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.impl.fabric.ShaderRegistryImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;

import java.io.IOException;

public class NucleusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CoreShaderRegistrationCallback.EVENT.register(context -> ShaderRegistryImpl.SHADERS.forEach(triple -> {
            try {
                context.register(triple.getLeft(), triple.getMiddle(), triple.getRight());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
