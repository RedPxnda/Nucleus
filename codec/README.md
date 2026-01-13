<h1>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="banner-white.svg">
        <img src="banner.svg" alt="Nucleus Codec">
    </picture> 
</h1>

# Usage
Simply use 
```java
 MapCodec<EaxmpleClass> CODEC = AutoCodec.of(ExampleClass.class);
```
or
```java 
 Codec<EaxmpleClass> CODEC = AutoCodec.of(ExampleClass.class).codec();
```
### Features

AutoCodec supports the following behaviors out of the box:

- **Default Values** – Use `setRecordDefaults(Map)` to provide fallback values for record components.
```java
public class TestClass{
  String optionalField = "default_value";
}
```
```java
public class TestRecord(String string){
    public static Codec<TestRecord> CODEC = AutoCodec.of(TestRecord.class)
            .setRecordDefaults(Map.of(
                    "string", () -> "default_string"
            ));
}
```
- **Optional / Nullable Fields** – Mark fields with `@CodecBehavior.Optional` to allow missing or null values.
```java
public record TestRecord(
@CodecBehavior.Optional String optionalField
) {}
```
- **Class-Level Defaults** – Configure default nullability and behavior using `@Settings`.
```java
@Settings(nullableByDefault = true)
public record TestRecord(String alwaysNullable) {}
```
- **Custom Field Names** – Rename serialized fields using `@AutoCodec.Name`.
```java
public record TestRecord (
        @AutoCodec.Name("json_name") String javaField
) implements AdditionalConstructing {
    public void additionalSetup(){
        //Custom logic while javaField is already set by the AutoCodec
    }
        }
```
Serialized as "json_name" instead of "javaField"
- **Ignored Fields** – Exclude fields from encoding/decoding with `@Ignored`.
```java
public record TestRecord(
        String included,
        @Ignored String ignored
) {}
```
"ignored" field is skipped during encoding/decoding
- **Custom Codecs** – Override field codecs using `@CodecBehavior.Override`.
Consider using CodecBehaviour.registerClass(Class<?>, Codec<?> codec); to register the codec instead.
```java
public record TestRecord(
        @CodecBehavior.Override("CUSTOM_CODEC") CustomType field
) {
  public static @SuppressWarnings("unused") Codec<CustomType> CUSTOM_CODEC = CustomType.CUSTOM_CODEC;
}
// Uses CUSTOM_CODEC for serialization instead of auto-generated one

```
- **Post-Decode Logic** – Apply additional construction logic by implementing `AdditionalConstructing`.
```java
public record TestRecord(
        @CodecBehavior.Optional String optionalField
) {}
```

To register Codecs used in AutoCodecs, simply call
```java 
static {
    CodecBehaviour.registerClass(Integer.class, Codec.INT);
}
```
## AutoCodec – Supported Classes (Default Registrations)

### Primitive Types and Boxed Variants
- Integer / int
- Double / double
- Float / float
- Boolean / boolean
- Byte / byte
- Short / short
- Long / long
- String

### Minecraft / Platform Types
- ResourceLocation
- Component
- Color
- Vector3f
- InterpolateMode
- ParticleOptions

### Functional / Utility Types
- DoubleSupplier.Instance
- Pair<T, U>
- Either<L, R>

### Registry / Tag Types
- TagKey<T>
- TaggableEntry<T>
- TagList<T>

### Registry-Derived Types (Auto-Registered)
- All classes present in `MiscUtil.objectsToRegistries`  
  (each mapped to its corresponding `Registry#byNameCodec`)


**⚠️ Depends on Nucleus Core**
## Technical details

This is Nucleus's codec module. It includes utilities relating to serialization and deserialization, such as the [AutoCodec](https://github.com/RedPxnda/Nucleus/blob/1.20.1/codec/codec-common/src/main/java/com/redpxnda/nucleus/codec/auto/AutoCodec.java).
> AutoCodecs are comparable to Gson. They automatically generate a codec from some class, scanning fields to add as parameters. Inevitably, as auto-generation tends to be, AutoCodecs can 
> be unstable. They require classes to either: <br>
> 1. *Override* AutoCodec reading by either being present within the AutoCodec#inheritOverrides
> map or by using the AutoCodec.Override annotation. <br>
> 2. Have an available nullary constructor (no arguments) for use in AutoCodec-ception. (AutoCodecs generate more AutoCodecs, if needed.)
> This is nolonger a full requirement, you can either supply a custom constructor by calling AutoCodec.setClassConstructor(). This ONLY works for classes, not records.
> If no Constructor is found it will attempt to make one available using misc.Unsafe but it will still log an error in the console. DO NOT RELY ON THIS WORKING.
> sun.misc.Unsafe is Dangerous to be used like this.
> 3. When using Maps in classes use Map and not HashMap or other derivatives!

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
