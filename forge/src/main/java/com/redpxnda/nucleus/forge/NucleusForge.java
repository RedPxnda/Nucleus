package com.redpxnda.nucleus.forge;

import dev.architectury.platform.forge.EventBuses;
import com.redpxnda.nucleus.Nucleus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

@Mod(MOD_ID)
public class NucleusForge {
    public NucleusForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Nucleus.init();
    }

    public static class ClientEvents {
        @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
        public static class ModBus {
            @SubscribeEvent
            public static void onShaderRegistration(RegisterShadersEvent event) {
                event.registerShader(Shaders.TEST_SHADER_FUNC.apply(event.getResourceManager()), shdr -> Shaders.TEST_SHADER = shdr);
            }
        }
    }
}
