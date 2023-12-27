<h1>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="banner-white.svg">
        <img src="banner.svg" alt="Nucleus Config">
    </picture> 
</h1>

**⚠️ Depends on Nucleus Core and Nucleus Codec**

This is Nucleus's config module, built using codecs. This allows for easy and automatic entry evaluation so that you can do a minimal amount of work.
Item, Block, EntityType, Text, Color, Identifier, and anything Minecraft has a registry for can be used as a field. If that's not enough, you can easily
specify a custom (de)serialization method using a codec. See [AutoCodec](https://github.com/RedPxnda/Nucleus/blob/refactor/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/AutoCodec.java)
and [Nucleus's Codec Module](https://github.com/RedPxnda/Nucleus/tree/refactor/codec/README.md).

Features summary:
* Field scanning - defining your config's format is as easy as creating a normal class with normal fields
* File watching(can be disabled) - players can quickly reload configs without needing to restart their games
* Automatic synchronization - automatically sync your server config to clients
* Environment-based evaluation control - define whether your config is client only, server only, or both

Here's a basic example of creating a config:
```java
ConfigManager.register(ConfigBuilder.automatic(NucleusConfig.class) // two options: automatic, which scans fields(using ConfigAutoCodec), or custom, which uses a custom codec
    .name("nucleus") // the name of this config. This will be your file's name
    .creator(NucleusConfig::new) // the way to create a default/empty instance of this config
    .type(ConfigType.COMMON) // the type of config. Can be CLIENT, COMMON, SERVER, or SERVER_CLIENT_SYNCED
    .updateListener(config -> NucleusConfig.INSTANCE = config) // a listener for whenever the config is updated
);
```
For more customization, see [ConfigBuilder](https://github.com/RedPxnda/Nucleus/blob/refactor/config/config-common/src/main/java/com/redpxnda/nucleus/config/ConfigBuilder.java).
For other usage, like retrieving registered configs, see [ConfigManager](https://github.com/RedPxnda/Nucleus/blob/refactor/config/config-common/src/main/java/com/redpxnda/nucleus/config/ConfigManager.java).
For a description of the available config types, see [ConfigType](https://github.com/RedPxnda/Nucleus/blob/refactor/config/config-common/src/main/java/com/redpxnda/nucleus/config/ConfigType.java).

Planned features:
* In game modification gui
* Config presets