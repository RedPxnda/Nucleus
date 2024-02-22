package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.codec.tag.EntityTypeList;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.config.preset.ConfigProvider;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.FloatRange;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigAutoCodec.ConfigClassMarker
public class TestConfig {
    public static TestConfig INSTANCE = null;

    public enum TestEnum {
        first,
        second,
        third,
        fourth,
        fifth,
        another,
        last
    }

    public enum TestPreset implements ConfigProvider<TestConfig> {
        @Comment("Some: a preset where some!")
        some,
        @Comment("preset is a preset where -> dafdjlalfgkjel")
        preset,
        another,
        and,
        @Comment("other some")
        someOther;

        @Override
        public TestConfig getInstance() {
            return new TestConfig();
        }
    }

    public int[] primArray = {1, 2, 3};
    public String[] array = {"1", "two", "3rd"};

    @Comment("presett!!!")
    public ConfigPreset<TestConfig, TestPreset> preset = ConfigPreset.none();

    @Comment("This is a collection test")
    public List<TestEnum> list = new ArrayList<>();

    public TestEnum anEnum = TestEnum.first;

    public Map<String, Integer> map = new HashMap<>();

    public InnerConfig inner = new InnerConfig();

    @Comment("This field is an optional integer.")
    @CodecBehavior.Optional
    public Integer someKoolInteger = 5;

    @FloatRange(max = 100)
    public float aFloat = 67.5f;

    @Comment("here's a comment on an OPTIONAL string field")
    @CodecBehavior.Optional
    public String str = "aggggh!";

    public Identifier identifier = new Identifier("abcd");

    public Item item = Items.ACACIA_LOG;

    public Color color = new Color();

    public TagKey<EntityType<?>> entityTypeTag = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier("sussy:tag"));

    public EntityTypeList entityTypeList = EntityTypeList.of();

    @ConfigAutoCodec.ConfigClassMarker
    public static class InnerConfig {
        @Comment("inner config, inner integer")
        @CodecBehavior.Optional
        public Integer more = 5;

        public String innerValues = "aggggh!";
    }

    /*@Comment("This is a list of entity types, or entity type tags!")
    public EntityTypeList entities = EntityTypeList.of();

    @Comment("This is a list of items, or item tags!")
    public ItemList items = ItemList.of(Items.APPLE);*/


    /*public enum Presets implements ConfigProvider<TestConfig> {
        entities(() -> MiscUtil.initialize(new TestConfig(), c -> {
            c.entities = EntityTypeList.of(EntityType.ALLAY, EntityType.ARROW, EntityType.AXOLOTL);
            c.items = ItemList.of();
            c.someKoolInteger = 1;
        })),
        bedrock(() -> MiscUtil.initialize(new TestConfig(), c -> {
            c.entities = EntityTypeList.of(EntityType.SHEEP);
            c.items = ItemList.of(Items.BEDROCK);
            c.someKoolInteger = 100;
        }));

        private final ConfigProvider<TestConfig> provider;

        Presets(ConfigProvider<TestConfig> provider) {
            this.provider = provider;
        }

        @Override
        public TestConfig getInstance() {
            return provider.getInstance();
        }
    }*/
}
