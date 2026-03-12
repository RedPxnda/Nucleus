package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.registration.ItemGroupCreator;
import com.redpxnda.nucleus.registration.RegistryId;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TestRegistries {
    @RegistryId("cool_item")
    public static final CompassItem testItem = new CompassItem(new Item.Properties());

    @RegistryId("cool_trinket_item")
    public static final TrinketItem trinketItem = new TrinketItem(new Item.Properties());

    @RegistryId("cool_block")
    public static final Block coolBlock = new Block(BlockBehaviour.Properties.of());

    @RegistryId("cool_tab")
    public static final CreativeModeTab group = ItemGroupCreator.populate(
            CreativeTabRegistry.create(Component.literal("YOOO WASSGOOD"), () -> Items.STICK.getDefaultInstance()),
            testItem,trinketItem
    );
}
