package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

@AutoCodec.Override()
public class BlockList extends TagList<Block> {
    public static final Codec<BlockList> CODEC = getCodec(BlockList::new, Registries.BLOCK, RegistryKeys.BLOCK);

    public static BlockList of() {
        return new BlockList(List.of(), List.of());
    }

    public static BlockList of(Block... blocks) {
        return new BlockList(List.of(blocks), List.of());
    }

    @SafeVarargs
    public static BlockList of(TagKey<Block>... tags) {
        return new BlockList(List.of(), List.of(tags));
    }

    public BlockList(List<Block> objects, List<TagKey<Block>> tags) {
        super(objects, tags, Registries.BLOCK, RegistryKeys.BLOCK);
    }

    public boolean contains(BlockState block) {
        return contains(block.getBlock());
    }
}
