package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.impl.EntityDataManager;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.impl.forge.EntityDataRegistryImpl;
import com.redpxnda.nucleus.impl.forge.ShaderRegistryImpl;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
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
        EntityEvent.ADD.register((entity, world) -> {
            if (!world.isClientSide)
                EntityDataRegistryImpl.CAPABILITIES.forEach((cls, holder) -> {
                    if (holder.predicate.test(entity)) {
                        EntityDataRegistryImpl.LISTENERS.get(cls).forEach(listener -> {
                            ((EntityDataRegistry.CreationListener) listener).onCreate(entity, EntityDataManager.getCapability(entity, cls));
                        });
                    }
                });
            return EventResult.pass();
        });
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
