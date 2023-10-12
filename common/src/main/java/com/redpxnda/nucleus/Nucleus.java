package com.redpxnda.nucleus;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.facet.FacetRegistry;
import com.redpxnda.nucleus.facet.TrackingUpdateSyncer;
import com.redpxnda.nucleus.facet.doubles.CapabilityRegistryListener;
import com.redpxnda.nucleus.facet.doubles.NumericalsFacet;
import com.redpxnda.nucleus.math.evalex.ListContains;
import com.redpxnda.nucleus.math.evalex.Switch;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.*;
import com.redpxnda.nucleus.pose.ClientPoseFacet;
import com.redpxnda.nucleus.pose.PoseAnimationResourceListener;
import com.redpxnda.nucleus.pose.ServerPoseFacet;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.resolving.wrappers.Wrappers;
import com.redpxnda.nucleus.util.MiscUtil;
import com.redpxnda.nucleus.util.Operation;
import com.redpxnda.nucleus.util.ReloadSyncPackets;
import com.redpxnda.nucleus.util.SupporterUtil;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Nucleus {
    public static final ExpressionConfiguration EXPRESSION_CONFIG = ExpressionConfiguration.defaultConfiguration().withAdditionalFunctions(
            Map.entry("LIST_HAS", new ListContains()),
            Map.entry("SWITCH", new Switch())
    );
    public static final String MOD_ID = "nucleus";
    public static final NetworkChannel CHANNEL = NetworkChannel.create(loc("main"));
    public static final Gson GSON = new Gson();
    public static final Logger LOGGER = LogUtils.getLogger();
    public static MinecraftServer SERVER;
    private static final List<String> ADDON_NAMESPACES = MiscUtil.initialize(new ArrayList<>(), l -> l.add("nucleus"));

    public static void init() {
        reloadListeners();
        packets();
        events();
        capabilities();
        SupporterUtil.init();
        AutoCodec.init();
        NucleusRegistries.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Rendering::init);
        ReloadSyncPackets.init();
        TrackingUpdateSyncer.init();
        Wrappers.init();
        Operation.init();

        LifecycleEvent.SERVER_BEFORE_START.register(server -> SERVER = server);

        // temp test code
        /*if (Platform.isDevelopmentEnvironment()) {
            InteractionEvent.RIGHT_CLICK_ITEM.register((p, hand) -> {
                if (p instanceof ServerPlayerEntity player) {
                    *//*if (player.getMainHandStack().isOf(Items.STICK) || player.getMainHandStack().isOf(Items.SADDLE)) {
                        double amnt = player.getMainHandStack().isOf(Items.STICK) ? 5 : -5;
                        NumericalsFacet cap = NumericalsFacet.getAllFor(player);
                        double val = cap.get("nucleus:test");
                        cap.set("nucleus:test", val + amnt);
                        cap.sendToClient(player);
                    } else if (player.getMainHandStack().isOf(Items.ALLIUM)) {
                        ServerPoseFacet cap = ServerPoseFacet.getFor(player);
                        String animation = cap.getPose().equals("none") ? "nucleus:test" : "none";
                        Hand usedHand = !player.isSneaking() ? Hand.MAIN_HAND : Hand.OFF_HAND;
                        cap.set(animation, player.getWorld().getTime(), usedHand);
                        cap.sendToClient(player);
                    }*//*
                    *//*if (player.getMainHandStack().isOf(Items.STICK)) {
                        ItemStack stack = player.getMainHandStack();
                        TestItemFacet cap = TestItemFacet.KEY.get(stack);
                        System.out.println(cap + " is the cap!");
                        if (cap != null) {
                            cap.val++;
                            cap.updateNbtOf(TestItemFacet.KEY, stack);
                            System.out.println("Set to: " + cap.val);
                            System.out.println("Item nbt: " + stack.getNbt());
                        }
                    }*//*
                }
                return CompoundEventResult.pass();
            });
        }*/
    }

    private static void packets() {
        registerPacket(ParticleCreationPacket.class, ParticleCreationPacket::new);
        registerPacket(PlaySoundPacket.class, PlaySoundPacket::new);
        registerPacket(FacetSyncPacket.class, FacetSyncPacket::new);
        registerPacket(DoublesFacetSyncPacket.class, DoublesFacetSyncPacket::new);
        registerPacket(PoseFacetSyncPacket.class, PoseFacetSyncPacket::new);
        registerPacket(SyncCapabilitiesJsonPacket.class, SyncCapabilitiesJsonPacket::new); // todo redo this guy
    }
    private static void events() {
    }
    private static void capabilities() {
        // Entities
        NumericalsFacet.KEY = FacetRegistry.register(loc("entity_numericals"), NumericalsFacet.class);
        ServerPoseFacet.KEY = FacetRegistry.register(loc("entity_pose"), ServerPoseFacet.class);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () ->
                ClientPoseFacet.KEY = FacetRegistry.register(loc("entity_pose_client"), ClientPoseFacet.class)
        );

        // ItemStacks
        //TestItemFacet.KEY = FacetRegistry.register(loc("test_item_facet"), TestItemFacet.class);

        // Status Effect Instances
        //TestStatusEffectFacet.KEY = FacetRegistry.register(loc("test_status_effect_facet"), TestStatusEffectFacet.class);

        // Attachment
        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            attacher.add(NumericalsFacet.KEY, new NumericalsFacet(entity));

            if (entity instanceof PlayerEntity) {
                if (!entity.getWorld().isClient) attacher.add(ServerPoseFacet.KEY, new ServerPoseFacet(entity));
                else attacher.add(ClientPoseFacet.KEY, new ClientPoseFacet(entity));
            }
        });
        /*FacetRegistry.ITEM_FACET_ATTACHMENT.register((stack, attacher) -> {
            if (MiscUtil.isItemOfIgnoringCount(stack, Items.STICK) && SERVER != null) attacher.add(TestItemFacet.KEY, new TestItemFacet());
        });*/
        /*FacetRegistry.STATUS_EFFECT_FACET_ATTACHMENT.register((object, attacher) -> {
            attacher.add(TestStatusEffectFacet.KEY, new TestStatusEffectFacet());
        });*/
    }

    public static <T extends SimplePacket> void registerPacket(Class<T> cls, Function<PacketByteBuf, T> decoder) {
        CHANNEL.register(cls, T::toBuffer, decoder, T::wrappedHandle);
    }

    public static Identifier loc(String str) {
        return new Identifier(MOD_ID, str);
    }

    /*public static class TestStatusEffectFacet implements StatusEffectFacet<TestStatusEffectFacet, NbtDouble> {
        public static FacetKey<TestStatusEffectFacet> KEY;

        public double val = 0;

        @Override
        public NbtDouble toNbt() {
            return NbtDouble.of(val);
        }

        @Override
        public void loadNbt(NbtDouble nbt) {
            val = nbt.doubleValue();
        }

        @Override
        public void applyEffectUpdate(LivingEntity entity, StatusEffectInstance instance) {
            val++;
        }

        @Override
        public void onApplied(LivingEntity entity, StatusEffectInstance instance) {
            entity.setOnFireFor(2);
            val+=5;
        }
    }*/

    /*public static class TestItemFacet implements ItemStackFacet<TestItemFacet, NbtDouble> {
        public static FacetKey<TestItemFacet> KEY;

        public double val = 5;

        public TestItemFacet() {
            //System.out.println("bruv: " + val);
            //new Throwable().printStackTrace();
        }
        public TestItemFacet(double val) {
            this.val = val;
        }

        @Override
        public NbtDouble toNbt() {
            return NbtDouble.of(val);
        }

        @Override
        public void loadNbt(NbtDouble tag) {
            val = tag.doubleValue();
        }

        @Override
        public void onCopied(TestItemFacet original) {
            //ItemStackFacet.super.onCopied(original);
            val = original.val;
        }
    }*/

    private static void reloadListeners() {
        ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new CapabilityRegistryListener()); // works for nucleus and addon namespaces
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new PoseAnimationResourceListener())); // works for nucleus and addon namespaces
    }

    public static void addAddonNamespace(String namespace) {
        ADDON_NAMESPACES.add(namespace);
    }
    public static boolean isNamespaceValid(String namespace) {
        return ADDON_NAMESPACES.contains(namespace);
    }
}