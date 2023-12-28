package com.redpxnda.nucleus.trinket.forge;

import com.redpxnda.nucleus.trinket.NucleusTrinket;
import com.redpxnda.nucleus.trinket.impl.forge.TrinketItemCreatorImpl;
import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusTrinket.MOD_ID)
public class NucleusTrinketForge {
    public NucleusTrinketForge() {
        EventBuses.registerModEventBus(NucleusTrinket.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusTrinket.init();

        if (Platform.isModLoaded("curios")) {
            MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, TrinketItemCreatorImpl::attachCuriosCaps);
        }
    }
}
