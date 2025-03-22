package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.config.ConfigBuilder;
import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.config.ConfigType;
import com.redpxnda.nucleus.editor.core.ClientLoader;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.registration.RegistryAnalyzer;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;


public class NucleusTest {
    public static final String MOD_ID = "nucleus_test";
    public static final ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("nucleus", "item/blank");

    public static void init() {
        /*
        ███████╗ █████╗  ██████╗███████╗████████╗███████╗
        ██╔════╝██╔══██╗██╔════╝██╔════╝╚══██╔══╝██╔════╝
        █████╗  ███████║██║     █████╗     ██║   ███████╗
        ██╔══╝  ██╔══██║██║     ██╔══╝     ██║   ╚════██║
        ██║     ██║  ██║╚██████╗███████╗   ██║   ███████║
        ╚═╝     ╚═╝  ╚═╝ ╚═════╝╚══════╝   ╚═╝   ╚══════╝
        */
        CoolEntityFacet.KEY = FacetRegistry.register(ResourceLocation.fromNamespaceAndPath("example", "cool_entity_facet"), CoolEntityFacet.class);

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            if (entity instanceof Player)
                attacher.add(CoolEntityFacet.KEY, new CoolEntityFacet(entity));
        });

        PlayerEvent.PLAYER_JOIN.register(player -> {
            CoolEntityFacet facet = CoolEntityFacet.KEY.get(player);
            if (facet != null) {
                facet.someIntegerValue++;
                facet.sendToClient(player);
            }
        });

        /*
         ██████╗ ██████╗ ███╗   ██╗███████╗██╗ ██████╗ ███████╗
        ██╔════╝██╔═══██╗████╗  ██║██╔════╝██║██╔════╝ ██╔════╝
        ██║     ██║   ██║██╔██╗ ██║█████╗  ██║██║  ███╗███████╗
        ██║     ██║   ██║██║╚██╗██║██╔══╝  ██║██║   ██║╚════██║
        ╚██████╗╚██████╔╝██║ ╚████║██║     ██║╚██████╔╝███████║
         ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝     ╚═╝ ╚═════╝ ╚══════╝
        */
        ConfigManager.register(ConfigBuilder.automatic(TestConfig.class)
                .id("nucleus:test-common")
                .fileLocation("nucleus/test")
                .type(ConfigType.COMMON)
                .creator(TestConfig::new)
                .updateListener(i -> TestConfig.INSTANCE = i)
                .presetGetter(i -> i.preset)
        );

        if (Platform.getEnv() == EnvType.CLIENT)
            ConfigManager.CONFIG_SCREENS_REGISTRY.register(r -> r.add("nucleus_test", ResourceLocation.parse("nucleus:test-common")));

        /*
        ██████╗ ███████╗ ██████╗ ██╗███████╗████████╗██████╗ ██╗███████╗███████╗
        ██╔══██╗██╔════╝██╔════╝ ██║██╔════╝╚══██╔══╝██╔══██╗██║██╔════╝██╔════╝
        ██████╔╝█████╗  ██║  ███╗██║███████╗   ██║   ██████╔╝██║█████╗  ███████╗
        ██╔══██╗██╔══╝  ██║   ██║██║╚════██║   ██║   ██╔══██╗██║██╔══╝  ╚════██║
        ██║  ██║███████╗╚██████╔╝██║███████║   ██║   ██║  ██║██║███████╗███████║
        ╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚══════╝╚══════╝
         */
        RegistryAnalyzer.register("nucleus", () -> TestRegistries.class);
        /*
        EDITOR
        */
        if (true) {
            ClientLoader.RENDER.add(ImGuiDebugPanels::onRender);
        }

    }
}
