package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.impl.forge.EntityDataRegistryImpl;
import com.redpxnda.nucleus.impl.forge.ShaderRegistryImpl;
import dev.architectury.platform.forge.EventBuses;
import com.redpxnda.nucleus.Nucleus;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

@Mod(MOD_ID)
public class NucleusForge {
    public NucleusForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Nucleus.init();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class ModEvents {
        @SubscribeEvent
        public static void onAttachCaps(final AttachCapabilitiesEvent<Entity> event) {
            EntityDataRegistryImpl.CAPABILITIES.forEach((clazz, cap) -> {
                if (cap.predicate.test(event.getObject()) && !event.getObject().getCapability(cap.capability).isPresent()) {
                    EntityDataRegistryImpl.CapProvider provider = ((Function<Object, EntityDataRegistryImpl.CapProvider>) cap.provider).apply(cap.capability);
                    event.addCapability(cap.id, provider);
                    event.addListener(provider::invalidate);
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterCaps(final RegisterCapabilitiesEvent event) {
            EntityDataRegistryImpl.CAPABILITIES.forEach((clazz, cap) -> {
                cap.capability = CapabilityManager.get(new CapabilityToken<>(){});
                event.register(clazz);
            });
        }
    }

    public static class ClientEvents {
        @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
        public static class ModBus {
            @SubscribeEvent
            public static void onShaderRegistration(RegisterShadersEvent event) {
                ShaderRegistryImpl.SHADERS.forEach(pair -> {
                    event.registerShader(pair.getFirst().apply(event.getResourceProvider()), pair.getSecond());
                });
            }
        }
    }
}
