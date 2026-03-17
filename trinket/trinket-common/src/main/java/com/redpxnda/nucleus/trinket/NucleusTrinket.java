package com.redpxnda.nucleus.trinket;


import com.google.common.base.Suppliers;
import com.redpxnda.nucleus.trinket.accessories.AccessoriesApiMirror;
import com.redpxnda.nucleus.trinket.curio.CurioApiMirror;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrarManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NucleusTrinket {
    public static final String MOD_ID = "nucleus_trinket";
    private static final Map<Item, Trinket> ITEMS = new HashMap<>();
    @ApiStatus.Internal
    public static final List<TrinketApiMirror> CREATOR = new ArrayList<>();
    private static boolean initialized = false;
    public static boolean DONT_CRASH_ON_NO_CREATOR = false;
    public static Set<String> WATCHED_NAMESPACES = new HashSet<>();
    public static final Supplier<RegistrarManager> registrar = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    static {
        if (Platform.isModLoaded("accessories")) {
            registerCreator(new AccessoriesApiMirror());
        } else if (Platform.isModLoaded("curios")) {
            registerCreator(new CurioApiMirror());
        }
        flushRegistrations();
    }

    public static void register(Item item, Trinket trinket) {
        ITEMS.put(item, trinket);
        if (initialized) {
            CREATOR.forEach(c -> {
                if (c.enabled()) {
                    c.registerCurioTrinket(item, trinket);
                }
            });
        }
    }

    @Environment(EnvType.CLIENT)
    public static void registerRenderer(Item item, Supplier<TrinketRenderer> trinket) {
        NucleusTrinketClient.RENDERER.put(item, trinket);
        if (initialized) {
            CREATOR.forEach(c -> {
                if (c.enabled()) {
                    c.registerCurioTrinketRenderer(item, trinket);
                }
            });
        }
    }

    public static Map<CommonSlotReference, ItemStack> getItems(LivingEntity wearer) {
        Map<CommonSlotReference, ItemStack> map = new HashMap<>();
        CREATOR.forEach(c -> map.putAll(c.getItems(wearer)));
        return map;
    }

    public static List<ItemStack> getTrinketList(LivingEntity wearer) {
        List<ItemStack> list = new ArrayList<>();
        CREATOR.forEach(c -> list.addAll(c.getTrinketList(wearer)));
        return list;
    }

    public static void registerCreator(TrinketApiMirror creator) {
        CREATOR.add(creator);
        initialized = true;
        if (creator.enabled()) {
            for (var entry : ITEMS.entrySet()) {
                creator.registerCurioTrinket(entry.getKey(), entry.getValue());
            }
            if (Platform.getEnv() == EnvType.CLIENT) {
                NucleusTrinketClient.registerCreatorClient(creator);
            }
        }
    }

    public static boolean canBeUsed() {
        return initialized;
    }

    public static void init() {
        LifecycleEvent.SETUP.register(() -> {
            if (
                    !initialized &&
                    !ITEMS.isEmpty() &&
                    !DONT_CRASH_ON_NO_CREATOR) {
                ensureInitialized();
            }
        });
        registrar.get().get(Registries.DATA_COMPONENT_TYPE).register(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "trinket_slots"),
                () -> AdditionalSlotComponent.ADDITIONAL_SLOTS);
    }

    public static void ensureInitialized() {
        if (CREATOR.isEmpty()) {
            throw new IllegalStateException(
                    "Nucleus Trinket requires a supported trinket mod (Accessories or Curios) to be installed."
            );
        }
        initialized = true;
    }

    private static void flushRegistrations() {
        for (var entry : ITEMS.entrySet()) {
            CREATOR.forEach(c -> c.registerCurioTrinket(entry.getKey(), entry.getValue()));
        }
        if (Platform.getEnv() == EnvType.CLIENT) {
            NucleusTrinketClient.flushRegistrationsClient();
        }
    }

}
