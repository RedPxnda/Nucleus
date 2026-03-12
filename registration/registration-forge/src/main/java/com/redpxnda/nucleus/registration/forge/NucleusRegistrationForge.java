package com.redpxnda.nucleus.registration.forge;

import com.redpxnda.nucleus.registration.NucleusRegistration;
import com.redpxnda.nucleus.registration.RegistrationListener;
import com.redpxnda.nucleus.registration.RegistryAnalyzer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(NucleusRegistration.MOD_ID)
public class NucleusRegistrationForge {
    public NucleusRegistrationForge(final IEventBus modEventBus, final ModContainer modContainer) {
        NucleusRegistration.init();

        modEventBus.register(NucleusRegistrationForge.class);
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        for (Map.Entry<String, Tuple<Supplier<Map<ResourceKey<?>, Map<ResourceLocation, Object>>>, Consumer<Object>>> entry : RegistryAnalyzerImpl.registrationListeners.entries()) {
            var supplier = entry.getValue().getA();
            Consumer<Object> finishListener = entry.getValue().getB();
            Map<ResourceKey<?>, Map<ResourceLocation, Object>> map = supplier.get();

            ResourceKey key = event.getRegistryKey();
            Map<ResourceLocation, Object> objects = map.get(key);
            if (objects != null)
                objects.forEach((id, obj) -> {
                    event.register(key, id, () -> obj);
                    RegistrationListener.callAllFor(obj);
                });
            RegistryAnalyzer.LOGGER.info("Finished registration for mod '{}'.", entry.getKey());
        }
    }
}
