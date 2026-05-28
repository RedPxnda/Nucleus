<h1>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="banner-white.svg">
        <img src="banner.svg" alt="Nucleus">
    </picture> 
</h1>

Nucleus is a multiplatform all-purpose library. Each module has its own features and instructions, so read there.
- [Codec](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/README.md) (Components relating to data serialization)
- [Config](https://github.com/RedPxnda/Nucleus/blob/1.20.1/config/README.md) (Simple config api)
- Core (Base for everything - no readme)
- [Facet](https://github.com/RedPxnda/Nucleus/blob/1.20.1/facet/README.md) (Equivalent to Forge's Capability System)
- Pose (Simple player animation lib - no readme *yet*)
- Trinket (Accessories and Curios API abstraction) so you don't require either

## Maven
```groovy
repositories {
    maven {
        name "upcraftReleases" // tysm up for letting me use your maven
        url "https://maven.uuid.gg/releases"
    }
}

dependencies {
    // general example, may vary depending on what you're using. See below for proper examples.
    implementation "com.redpxnda.nucleus:nucleus-<MODULE>-<PLATFORM>:<MINECRAFT_VERSION>+<VERSION>"
}
```
On Architectury Loom(and possibly fabric loom), you should do:
```groovy
include(modApi("com.redpxnda.nucleus:nucleus-<MODULE>-<PLATFORM>:<MINECRAFT_VERSION>+<VERSION>"))

/* ex: 
include(modApi("com.redpxnda.nucleus:nucleus-core-fabric:1.20.1+1.0.0"))\
*/
```
On NeoGradle(might need an extra fg.deobf on ForgeGradle):
```groovy
implementation(jarJar("com.redpxnda.nucleus:nucleus-<MODULE>-<PLATFORM>:<MINECRAFT_VERSION>+<VERSION>")) {
    jarJar.ranged(it, "[<VERSION>,)")
}

/*ex: 
implementation(jarJar("com.redpxnda.nucleus:nucleus-codec-forge:1.20.1+1.0.0")) {
    jarJar.ranged(it, "[1.0.0,)")
}
*/
```
