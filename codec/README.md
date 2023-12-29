<h1>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="banner-white.svg">
        <img src="banner.svg" alt="Nucleus Codec">
    </picture> 
</h1>

**⚠️ Depends on Nucleus Core**

This is Nucleus's codec module. It includes utilities relating to serialization and deserialization, such as the [AutoCodec](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/auto/AutoCodec.java).
> AutoCodecs are comparable to Gson. They automatically generate a codec from some class, scanning fields to add as parameters. Inevitably, as auto-generation tends to be, AutoCodecs can 
> be unstable. They require classes to either: <br>
> 1. *Override* AutoCodec reading by either being present within the AutoCodec#inheritOverrides
> map or by using the AutoCodec.Override annotation. <br>
> 2. Have an available nullary constructor (no arguments) for use in AutoCodec-ception. (AutoCodecs generate more AutoCodecs, if needed.) 
> This is the case because creating object instances without constructors is incredibly dangerous. (See sun.misc.Unsafe) <br>
>
> If neither of these conditions are met, errors will be thrown. So be wary. If you want to use a class in
> your AutoCodec that doesn't meet these conditions, you can either add the class to the AutoCodec#inheritOverrides,
> or you can use AutoCodec.Override on a field to specify a separate codec stored in a static field to use for it. <br>
> Try to avoid using Wildcards and Type parameters with AutoCodecs, especially for Collections and Maps.
> Be extremely careful when using Maps of Maps. As long as your keys and values are marked as *Map*s, and not some Map implementation,
> you should be fine. (For example: *Map<Map<String>, Map<Integer>>* will work. However, *Map<HashMap<String>, HashMap<Integer>>*
> will not work since you are enforcing HashMap rather than allowing general Maps.) This is hard to fix, since performance becomes
> an issue incredibly quickly when trying to translate maps into the form you desire.
> Use AutoCodec#of(Class) in order to create a new AutoCodec. Then use AutoCodec#codec() to
> obtain the Codec object for encoding and decoding. <br>

Other utilities include the [CompactableMapCodec](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/misc/CompactableMapCodec.java),
which is:
> A codec representing a map, but in two ways:
> ```
>     {
>         "someKey": "someValue"
>     }
> ```
> OR
> <code>["someKey|someValue"]</code> (but it will encode as the former)

[TagLists](https://github.com/RedPxnda/Nucleus/tree/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/util/tag), a handy way of creating a list
that allows both tags and normal entries. Tag entries begin with a *#*. [EntityTypeLists](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/tag/EntityTypeList.java)
also allow entries beginning with a *$*, representing hardcoded checks. These checks are expandable, but by default will include:
* "tamables" - for any *Tameable*(yarn) entity.
* "animals" - for any *AnimalEntity*. (pigs, sheep, etc.)
* "merchants" - for any *Merchant*. (villagers, wandering traders)
* "mobs" - for any *MobEntity*. (zombies, creepers, etc.)
* "chested_horses" - for horse-like entities who can hold a chest. (llamas, donkeys)
* "horse_likes" - for any horse-like entities: *AbstractHorseEntity*

And much more. 
