package com.redpxnda.nucleus.expression;

import com.redpxnda.nucleus.expression.mappings.MappingsDataLoader;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resource.ResourceType;

public class NucleusExpression {
    public static final String MOD_ID = "nucleus_expression";
    
    public static void init() {
        ReloadListenerRegistry.register(ResourceType.SERVER_DATA, MappingsDataLoader.INSTANCE);
        //EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, MappingsDataLoader.INSTANCE));
    }
}
