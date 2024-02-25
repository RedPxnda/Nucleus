<h1>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="banner-white.svg">
        <img src="banner.svg" alt="Nucleus Config">
    </picture> 
</h1>

**⚠️ Depends on Nucleus Core and Nucleus Codec**

This is Nucleus's config module, built using codecs. This allows for easy and automatic entry evaluation so that you can do a minimal amount of work.
Item, Block, EntityType, Text, Color, Identifier, and anything Minecraft has a registry for can be used as a field. If that's not enough, you can easily
specify a custom (de)serialization method using a codec. See [AutoCodec](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/auto/AutoCodec.java)
and [Nucleus's Codec Module](https://github.com/RedPxnda/Nucleus/tree/1.20.1/codec/README.md).
As of 1.1.0, you can also easily create an automatic in-game editing screen for your config.

Features summary:
* Field scanning - defining your config's format is as easy as creating a normal class with normal fields
* File watching(can be disabled) - players can quickly reload configs without needing to restart their games
* Automatic synchronization - automatically sync your server config to clients
* Environment-based evaluation control - define whether your config is client only, server only, or both
* (1.1.0+) Automatic screen creation - automatically create an in-game editor for your config
* (1.1.0+) Config presets - allow users to apply specific built-in presets to your config

Here's a basic example of creating a config:

Start by creating your config class, housing all of the fields of your config.
Note: Not *every* type will work, but most important ones will. See [CodecBehavior](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/behavior/CodecBehavior.java) to see the defined behaviors. If you want to add support for your own types, register it with the CodecBehavior class. 
(And for screen component support, see [ConfigComponentBehavior](https://github.com/RedPxnda/Nucleus/blob/1.20.1/config/config-common/src/main/java/com/redpxnda/nucleus/config/screen/component/ConfigComponentBehavior.java) 
```java
@ConfigAutoCodec.ConfigClassMarker // this annotation is required to make sure this class is correctly (de)serialized
public class TestConfig {
    public static TestConfig INSTANCE = new TestConfig(); // we store an instance of our config to use it wherever- we will update it later

    public float aFloat = 67.5f; // adding fields is exactly like adding fields to a normal class
    public String[] array = {"1", "two", "3rd"};
    public Identifier identifier = new Identifier("abcd"); // identifiers/resourcelocations are supported
    public Color color = new Color(); // colors, via Nucleus Core's Color class

    @AutoCodec.Name("item_but_new_name") // using this annotation, you can change the name of the field in the serialized json file
    public Item item = Items.ACACIA_LOG; // anything from vanilla's Registries class(BuiltinRegistries on mojmap) are supported

    @Comment("Here's a direction field!") // comments are supported using Nucleus Core's comment annotation. Use \n or triple quoted strings to make it multiline.
    public Direction anEnum = Direction.NORTH; // enums are supported as well

    @ConfigAutoCodec.ConfigClassMarker // remember this annotation! It's important for Nucleus Config to correctly create the codec for this class
    public static class InnerConfig { // you can also create "categories" of sorts by just nesting in a class
        @Comment("inner config, inner integer")
        @CodecBehavior.Optional // this annotation makes this field "optional". If it is unspecified in the json or set to null, it will set to its "default" value. In this case, 5.
        public Integer more = 5; // boxed primitive variants are also allowed

        @DoubleRange(min = 0, max = 100) // there are ranged annotations for doubles, integers, and floats. This will clamp the value within the specified range, with the option to throw an error if the value falls outside of it
        public double aDouble = 67.5;
    }

    public InnerConfig inner = new InnerConfig();

    
    public enum TestPreset implements ConfigProvider<TestConfig> { // If you want, you can also create "presets" for your config. Implement ConfigProvider like so
        @Comment("Some: a preset where your mod is configured in a way that is ideal for some!")
        SOME,

        ANOTHER_PRESET,

        @Comment("this comment is only really used for the in-game editing gui. if that's not of your concern, then this comment is unnecessary")
        SOME_OTHER_PRESET;

        @Override
        public TestConfig getInstance() {
            return new TestConfig(); // you will need to define a method to create an instance of your config for this preset. Obviously, you will want this method to return a different instance based on the current preset.
        }
    }

    public ConfigPreset<TestConfig, TestPreset> preset = ConfigPreset.none(); // now, add this preset to your config like so. "none" will always be an option as a preset, so there's no need to add it to your enum. We will have to do more later to make this preset functional, so keep that in mind
}
```

Now that you've created your class, you need to register it: (Do this in your mod's main class, or potentially on client start for client only configs.)
```java
ConfigManager.register(ConfigBuilder.automatic(TestConfig.class) // Here we start creating the builder for the config. "automatic" will automatically use the fields in our class. If you want to (de)serialize the json data yourself, specify your own codec with "custom" instead of "automatic". 
    .name("our_cool_config") // REQUIRED: Defines the name of this config. In this case, the file name will be "our_cool_config.jsonc"
    .creator(TestConfig::new) // REQUIRED: Defines the way to create a default/empty instance of this config
    .type(ConfigType.COMMON) // REQUIRED: Defines the type of config. Can be CLIENT, COMMON, SERVER, or SERVER_CLIENT_SYNCED(they are what they sound like, but see ConfigType if you need more info)
    .updateListener(theConfigInstance -> TestConfig.INSTANCE = theConfigInstance) // OPTIONAL(but heavily recommended): Defines a listener for whenever the config is updated. We use this to store the instance of our config.
    .presetGetter(theConfigInstance -> theConfigInstance.preset) // OPTIONAL: Defines a way to obtain the preset from our config instance
);
```

Additionally, you may want to have a screen for your config: (Do this on the client only to avoid dedicated server class-loading issues)
```java
/* This is an event that fires:
- When the ModMenu entrypoint fires on Fabric, requesting the config screens for mods
- When NucleusConfig loads on forge, providing config screens for the specified mods
You are given a "ScreenRegisterer", which has a few "add" methods. The one we are mainly concerned about has two string parameters. The first is the id of your mod, and the second is the name of your config(specified in registration above)
If you want custom screen behavior, you can also use the other "add" method to pass in a custom screen factory.
*/
ConfigManager.CONFIG_SCREENS_REGISTRY.register(screenRegisterer -> screenRegisterer.add("the_id_of_the_mod", "our_cool_config"));
```

For more customization, see [ConfigBuilder](https://github.com/RedPxnda/Nucleus/blob/1.20.1/config/config-common/src/main/java/com/redpxnda/nucleus/config/ConfigBuilder.java).
For other usage, like retrieving registered configs, see [ConfigManager](https://github.com/RedPxnda/Nucleus/blob/1.20.1/config/config-common/src/main/java/com/redpxnda/nucleus/config/ConfigManager.java).
For a description of the available config types, see [ConfigType](https://github.com/RedPxnda/Nucleus/blob/1.20.1/config/config-common/src/main/java/com/redpxnda/nucleus/config/ConfigType.java).
