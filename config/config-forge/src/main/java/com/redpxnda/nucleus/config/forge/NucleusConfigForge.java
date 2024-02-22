package com.redpxnda.nucleus.config.forge;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.config.ConfigScreensEvent;
import com.redpxnda.nucleus.config.NucleusConfig;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NucleusConfig.MOD_ID)
public class NucleusConfigForge {
    private static final Logger LOGGER = Nucleus.getLogger();

    public NucleusConfigForge() {
        EventBuses.registerModEventBus(NucleusConfig.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusConfig.init();
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = NucleusConfig.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ConfigScreensEvent.ScreenRegisterer registerer = new ConfigScreensEvent.ScreenRegisterer();
                ConfigManager.CONFIG_SCREENS_REGISTRY.invoker().add(registerer);
                registerer.getAllScreenFactories().forEach((id, factory) -> {
                    var optional = ModList.get().getModContainerById(id);
                    if (optional.isPresent())
                        optional.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((c, s) -> factory.apply(s)));
                    else
                        LOGGER.warn("Could not find mod '{}' to register config screen.", id);
                });
            });
        }
    }
}
