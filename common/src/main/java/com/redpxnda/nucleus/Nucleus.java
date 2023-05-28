package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.redpxnda.nucleus.datapack.json.listeners.ExampleListenerHandler;
import com.redpxnda.nucleus.datapack.lua.ExampleLuaListener;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;
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

    private static void reloadListenersStuff() {
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