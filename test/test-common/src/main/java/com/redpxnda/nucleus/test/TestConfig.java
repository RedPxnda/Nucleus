package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.codec.tag.EntityTypeList;
import com.redpxnda.nucleus.codec.tag.ItemList;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.config.preset.ConfigProvider;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

public class TestConfig {
    public static TestConfig INSTANCE = null;

    @Comment("This is a config preset!")
    public ConfigPreset<TestConfig, Presets> preset = ConfigPreset.none();

    @Comment("This field is an integer.")
    public int someKoolInteger = 5;

    @Comment("This is a list of entity types, or entity type tags!")
    public EntityTypeList entities = EntityTypeList.of();

    @Comment("This is a list of items, or item tags!")
    public ItemList items = ItemList.of(Items.APPLE);

    public enum Presets implements ConfigProvider<TestConfig> {
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
    }
}
