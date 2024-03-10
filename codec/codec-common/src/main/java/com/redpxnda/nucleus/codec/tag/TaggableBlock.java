package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;

@CodecBehavior.Override()
public class TaggableBlock extends TaggableEntry<Block> {
    public static final Codec<TaggableBlock> CODEC = new TaggableEntryCodec<>(TaggableBlock::new, TaggableBlock::new, Registries.BLOCK, RegistryKeys.BLOCK);

    public TaggableBlock(@NotNull Block object) {
        super(object, Registries.BLOCK, RegistryKeys.BLOCK);
    }

    public TaggableBlock(@NotNull TagKey<Block> tag) {
        super(tag, Registries.BLOCK, RegistryKeys.BLOCK);
    }
}
