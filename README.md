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
- Trinket (Trinket and Curios API abstraction - no readme *yet*)

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
    implementation "com.redpxnda.nucleus:<MODULE>-<PLATFORM>:<MINECRAFT_VERSION>+<VERSION>"
}
```
On Architectury Loom(and possibly fabric loom), you should do:
```groovy
include(modApi("com.redpxnda.nucleus:<MODULE>-<PLATFORM>:<MINECRAFT_VERSION>+<VERSION>"))

/* ex: 
include(modApi("com.redpxnda.nucleus:core-fabric:1.20.1+1.0.0"))\
*/
```
On ForgeGradle(and possibly NeoGradle):
```groovy
implementation(jarJar("com.redpxnda.nucleus:<MODULE>-<PLATFORM>:<MINECRAFT_VERSION>+<VERSION>")) {
    jarJar.ranged(it, "[<VERSION>,)")
}

/*ex: 
implementation(jarJar("com.redpxnda.nucleus:codec-forge:1.20.1+1.0.0")) {
    jarJar.ranged(it, "[1.0.0,)")
}
*/
```