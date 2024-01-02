package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.config.preset.ConfigProvider;
import com.redpxnda.nucleus.util.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Comment("presett!!!")
    public ConfigPreset<TestConfig, TestPreset> preset = ConfigPreset.none();

    @Comment("This is a collection test")
    public List<TestEnum> list = new ArrayList<>();

    public TestEnum anEnum = TestEnum.first;

    public Map<String, Integer> map = new HashMap<>();

    @Comment("This field is an integer.")
    public int someKoolInteger = 5;

    @Comment("here's a comment on a string field")
    public String string = "aggggh!";

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
