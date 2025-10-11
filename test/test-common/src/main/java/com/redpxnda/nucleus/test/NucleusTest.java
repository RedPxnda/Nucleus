package com.redpxnda.nucleus.test;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.config.ConfigBuilder;
import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.config.ConfigType;
import com.redpxnda.nucleus.editor.core.ClientLoader;
import com.redpxnda.nucleus.event.MiscEvents;
import com.redpxnda.nucleus.event.PrioritizedEvent;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.registration.RegistryAnalyzer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class NucleusTest {
    public static final String MOD_ID = "nucleus_test";
    public static final ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("nucleus", "item/blank");

    public static void init() {
        try {
            for (int i = 0; i < 1000; i++) {
                eventTest();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
        ███████╗██████╗ ██╗████████╗ ██████╗ ██████╗
        ██╔════╝██╔══██╗██║╚══██╔══╝██╔═══██╗██╔══██╗
        █████╗  ██║  ██║██║   ██║   ██║   ██║██████╔╝
        ██╔══╝  ██║  ██║██║   ██║   ██║   ██║██╔══██╗
        ███████╗██████╔╝██║   ██║   ╚██████╔╝██║  ██║
        ╚══════╝╚═════╝ ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
         */
        RegistryAnalyzer.register("nucleus", () -> TestRegistries.class);
        /*
        ███████╗██████╗ ██╗████████╗ ██████╗ ██████╗
        ██╔════╝██╔══██╗██║╚══██╔══╝██╔═══██╗██╔══██╗
        █████╗  ██║  ██║██║   ██║   ██║   ██║██████╔╝
        ██╔══╝  ██║  ██║██║   ██║   ██║   ██║██╔══██╗
        ███████╗██████╔╝██║   ██║   ╚██████╔╝██║  ██║
        ╚══════╝╚═════╝ ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
        */
        if (Platform.getEnv() == EnvType.CLIENT) {
            ClientLoader.RENDER.add(ImGuiDebugPanels::onRender);
        }

    }

    static PrioritizedEvent<MiscEvents.SingleInput<String>> EVENT = PrioritizedEvent.createEventResult();

    public static void eventTest() throws InterruptedException {
        int listenerCount = 100;
        int registrationThreads = 10;
        int invokerThreads = 50;


        ExecutorService executor = Executors.newFixedThreadPool(registrationThreads);

        CountDownLatch regDone = new CountDownLatch(listenerCount);

        // === Concurrent Registration ===
        for (int i = 0; i < listenerCount; i++) {
            final int index = i;
            executor.submit(() -> {
                String a = "test" + index;
                EVENT.register(input -> {
                    // Optionally add small delay to stress test
                    try {
                        Thread.sleep((int) (Math.random() * 5));
                    } catch (InterruptedException ignored) {
                    }
                    //System.out.println("Listener " + a + " received: " + input.getValue());
                    return EventResult.pass();
                });
                regDone.countDown();
            });
        }

        // Wait for all registrations to finish
        regDone.await();
        //System.out.println("All listeners registered.");

        // Optionally shutdown and recreate executor for clean thread pool
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // === Build the invoker once before calling it ===
        EVENT.invoker();

        // === Parallel Invocations ===
        ExecutorService invokerExecutor = Executors.newFixedThreadPool(invokerThreads);
        CountDownLatch ready = new CountDownLatch(invokerThreads);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(invokerThreads);

        Runnable callTask = () -> {
            try {
                ready.countDown();
                startSignal.await();
                EVENT.invoker().call("test");
            } catch (Exception e) {
                System.out.print(e);
                throw new RuntimeException(e);
            } finally {
                done.countDown();
            }
        };

        for (int i = 0; i < invokerThreads; i++) {
            invokerExecutor.submit(callTask);
        }

        ready.await();        // Wait for all threads to be ready
        startSignal.countDown();  // Start them all at once
        done.await();         // Wait for all to finish

        invokerExecutor.shutdown();
        //System.out.println("All invoker calls finished.");
        JsonObject object = new JsonObject();
        object.addProperty("test", "string");
        object.addProperty("number", 10);
        TestRecord test = AutoCodec.of(TestRecord.class).codec().decode(JsonOps.INSTANCE, object).result().get().getFirst();
        if (!"string".equals(test.testString())) {
            Nucleus.getLogger().warn("failed string record test");
        }
        if (!(10 == test.number())) {
            Nucleus.getLogger().warn("failed number test");
        }
    }

    public record TestRecord(@AutoCodec.Name("test") String testString, float number, @CodecBehavior.Optional String testing) {
    }
}
