package com.redpxnda.nucleus.test;

import com.google.gson.JsonNull;
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

        runAutoCodecTests();
    }

    public static void runAutoCodecTests() {
        // ------------------------------------------------------------
        // Test full decode: all fields present
        // ------------------------------------------------------------
        JsonObject fullObject = new JsonObject();
        fullObject.addProperty("test", "string");
        fullObject.addProperty("number", 10);
        fullObject.addProperty("opt_string", "hello");
        fullObject.addProperty("opt_int", 42);

        var fullResult = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, fullObject)
                .result();

        if (fullResult.isEmpty()) {
            Nucleus.getLogger().warn("❌ full record decode failed");
            return;
        }

        TestRecord full = fullResult.get().getFirst();

        if (!"string".equals(full.testString())) {
            Nucleus.getLogger().warn("❌ failed string field decode");
        }
        if (full.number() != 10) {
            Nucleus.getLogger().warn("❌ failed number field decode");
        }
        if (!"hello".equals(full.optionalString())) {
            Nucleus.getLogger().warn("❌ failed optional string decode");
        }
        if (full.optionalInt() != 42) {
            Nucleus.getLogger().warn("❌ failed optional int decode");
        }

        Nucleus.getLogger().info("✅ full record decode test passed");

        // ------------------------------------------------------------
        // Test missing optional fields (should succeed)
        // ------------------------------------------------------------
        JsonObject missingOptional = new JsonObject();
        missingOptional.addProperty("test", "required");
        missingOptional.addProperty("number", 5);

        var resultOptional = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, missingOptional)
                .result();

        if (resultOptional.isEmpty()) {
            Nucleus.getLogger().warn("❌ decode failed when optional fields were missing");
        } else {
            TestRecord record = resultOptional.get().getFirst();
            if (!"required".equals(record.testString()) || record.number() != 5) {
                Nucleus.getLogger().warn("❌ incorrect values on missing-optional test");
            }
            if (record.optionalString() != null || record.optionalInt() != null) {
                Nucleus.getLogger().warn("❌ optional fields not null when missing");
            } else {
                Nucleus.getLogger().info("✅ missing optional fields handled correctly");
            }
        }

        // ------------------------------------------------------------
        // Test missing required field (should fail)
        // ------------------------------------------------------------
        JsonObject missingRequired = new JsonObject();
        missingRequired.addProperty("number", 10);

        var resultMissingRequired = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, missingRequired)
                .result();

        if (resultMissingRequired.isEmpty()) {
            Nucleus.getLogger().info("✅ missing required field correctly caused failure");
        } else {
            Nucleus.getLogger().warn("❌ missing required field did not cause failure");
        }

        // ------------------------------------------------------------
        // Test renamed JSON field (Name annotation)
        // ------------------------------------------------------------
        JsonObject renamedObject = new JsonObject();
        renamedObject.addProperty("test", "renamed");
        renamedObject.addProperty("number", 99);
        renamedObject.addProperty("testString", "base name");

        var renamedResult = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, renamedObject)
                .result();

        if (renamedResult.isEmpty()) {
            Nucleus.getLogger().warn("❌ decode failed for renamed JSON fields");
        } else {
            TestRecord record = renamedResult.get().getFirst();
            if (!"renamed".equals(record.testString()) || record.number() != 99) {
                Nucleus.getLogger().warn("❌ renamed JSON fields decoded incorrectly");
            } else {
                Nucleus.getLogger().info("✅ renamed JSON fields handled correctly via @CodecBehaviour.Name");
            }
        }

        // ------------------------------------------------------------
        // Test null optional field (should succeed)
        // ------------------------------------------------------------
        JsonObject nullOptional = new JsonObject();
        nullOptional.addProperty("test", "nullable");
        nullOptional.addProperty("number", 1);
        nullOptional.add("opt_string", JsonNull.INSTANCE);

        var nullResult = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, nullOptional)
                .result();

        if (nullResult.isEmpty()) {
            Nucleus.getLogger().warn("❌ decode failed when optional field explicitly null");
        } else {
            TestRecord record = nullResult.get().getFirst();
            if (record.optionalString() != null) {
                Nucleus.getLogger().warn("❌ optional field not null when explicitly null");
            } else {
                Nucleus.getLogger().info("✅ optional field correctly handled when explicitly null");
            }
        }
    }


    public record TestRecord(
            @AutoCodec.Name("test") String testString,
            @AutoCodec.Name("number") int number,
            @CodecBehavior.Optional @AutoCodec.Name("opt_string") String optionalString,
            @CodecBehavior.Optional @AutoCodec.Name("opt_int") Integer optionalInt
    ) {}


    static PrioritizedEvent<MiscEvents.SingleInput<String>> EVENT = PrioritizedEvent.createEventResult();

    /**
     * earlier events sometimes produced concurrent exceptions
     * @throws InterruptedException
     */
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
    }
}
