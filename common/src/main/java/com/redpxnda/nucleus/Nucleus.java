package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.datapack.json.listeners.ExampleListenerHandler;
import com.redpxnda.nucleus.datapack.lua.ExampleLuaListener;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.Map;

public class Nucleus {
    public static final ExpressionConfiguration EXPRESSION_CONFIG = ExpressionConfiguration.defaultConfiguration().withAdditionalFunctions(
            Map.entry("LIST_HAS", new ListContains()),
            Map.entry("SWITCH", new Switch())
    );
    public static final String MOD_ID = "nucleus";

    public static void init() {
    }

    /*public static void particleProviders() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ParticleProviderRegistry.register(Particles.COOL_PARTICLE, new DynamicModelParticle.Provider(
                    () -> new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN)),
                    RenderType.entityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION),
                    p -> {},
                    p -> {},
                    (p, ps, cam) -> {}
            ));
        });
    }*/

    public static class TestEntityCap implements EntityCapability {
        public static void init() {
            EntityDataRegistry.register(new ResourceLocation(MOD_ID, "test"), e -> e instanceof Player, TestEntityCap.class, TestEntityCap::new);
            /*InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
                if (player.level.isClientSide) return CompoundEventResult.pass();
                if (player.getMainHandItem().is(Items.STICK)) {
                    TestEntityCap cap = EntityDataManager.getCapability(player, TestEntityCap.class);
                    System.out.println(cap.value++);
                }
                return CompoundEventResult.pass();
            });*/
        }

        private int value = 0;
        public int getValue() {
            return value;
        }

        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Int", value);
            return tag;
        }

        @Override
        public void loadNbt(CompoundTag tag) {
            value = tag.getInt("Int");
        }
    }

    private static void reloadListeners() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, ExampleListenerHandler.LISTENER);
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ExampleLuaListener());

        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            if (player.level.isClientSide) return CompoundEventResult.pass();
            if (player.getMainHandItem().is(Items.STICK)) {
                ExampleLuaListener.handlers.forEach((k, v) -> v.call(CoerceJavaToLua.coerce(new PlayerReference(player))));
            }

            return CompoundEventResult.pass();
        });
    }
}