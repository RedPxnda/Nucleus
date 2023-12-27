<h1>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="banner-white.svg">
        <img src="banner.svg" alt="Nucleus Facet">
    </picture>
</h1>

**⚠️ Depends on Nucleus Core**

This is Nucleus's facet module, an equivalent to [Forge's Capability System](https://forge.gemwire.uk/wiki/Capabilities) and [Cardinal Components API](https://github.com/Ladysnake/Cardinal-Components-API).
Why? Because I needed a multiplatform *consistent* data storage system, something that would not be provided by simply abstracting the two. (Trust me, I tried)

Essentially, facets are a way of attaching and saving data to various objects, such as entities. Rather than interacting with raw nbt, you have full control with
custom objects made and formatted by *you*. You can format and organize your data however you would like. The only thing you need to do is define how to turn your
data into nbt, and create it from nbt. 

Currently, you can attach facets to:
* Entities
* ItemStacks
* Status Effect Instances (MobEffectInstance on mojang mappings)

World, chunk, and block entity facets are planned.

## Usage
First, create a class implementing EntityFacet, ItemStackFacet, or StatusEffectFacet(etc.). If this is for some other unspecified usage, you can always just
implement Facet itself. ItemStackFacets and StatusEffectFacets have two type parameters while EntityFacet only has one. ItemStackFacet and StatusEffectFacet's
first type parameter should be your facet class. This may seem odd, but it's to ensure copying returns the correct type of facet. The next type parameter and
EntityFacet's only type parameter is the nbt type you serialize to and deserialize from. (How your facet will be stored.) 

Here's an example where we create an entity facet storing an integer and a string:

We start by creating our facet class.
```java
// Here is our class, serializing to a NbtCompound(CompoundTag on mojang mappings). NbtCompounds are like maps, or JSON objects.
public class CoolEntityFacet implements EntityFacet<NbtCompound> {
    public static FacetKey<CoolEntityFacet> KEY; // We will use this later to retrieve our facet from entities. Define it here for now.

    /* Here we have an object we want to store in the entity, with a default value of 0. 
    This is not an unusual format. It is no different from a normal java class, and that's exactly how you should view facets. */
    public int someIntegerValue = 0;

    // Another object, this time a string.
    public String someStringValue = "insert something clever here";

    // Here is a simple *optional* constructor that we will use later. I recommend you have this, it can be useful.
    public CoolEntityFacet(Entity entity) {}


    /* This is where we serialize our data, so it can be stored in the entity.
    (If you want a minimal amount of work, check out Nucleus's Codec module, specifically the AutoCodec. 
    In our case, and likely yours, there is no need, so we will do it traditionally for now.)*/
    @Override
    public NbtCompound toNbt() {
        NbtCompound compound = new NbtCompound(); // Let's start by creating our nbt compound.
        compound.putInt("someIntegerValue", someIntegerValue); // We can add some data to it...
        compound.putString("someStringValue", someStringValue);
        return compound; // and then return it. Bam! Easy as that.
        /* This will create the following structure:
        {
            "someIntegerValue": 0, // right now the value is set to 0. Realistically, this will be constantly changing.
            "someStringValue": "insert something clever here" // same as above ^
        }*/
    }

    // This is where we deserialize our data, for whenever the entity is loaded.
    @Override
    public void loadNbt(NbtCompound nbt) {
        someIntegerValue = nbt.getInt("someIntegerValue"); // we stored it as "someIntegerValue" earlier, here we retrieve it
        someStringValue = nbt.getString("someStringValue");
    }
}
```
Then, in our mod's main class, we register it.
```java
// This is a rough representation of your mod's main class. It obviously might not match, which is fine. 
// Just make sure you register your facet at an appropriate time.
public class ExampleMod {
    public static void init() {
        // Here we register our facet. The register method returns a FacetKey which we can use to interact with our facet.
        // Now you see our *KEY* field coming in to play. We assign it here. (For mojmap users: Identifier is the same as ResourceLocation.)
        CoolEntityFacet.KEY = FacetRegistry.register(new Identifier("example","cool_entity_facet"), CoolEntityFacet.class);

        // This is the entity facet attachment event. Use this to initially add your facets to entities.
        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            // Simple check to see if the entity is a player. This is how we control what entities do and don't have our facet.
            if (entity instanceof PlayerEntity)
                // Use the provided 'attacher' to add your facet to the entity.
                // You can see we use the constructor you made earlier. If you didn't add it, that's fine. Just don't pass in the entity.
                attacher.add(CoolEntityFacet.KEY, new CoolEntityFacet(entity));
        });
        
        // That's it! Our facet is registered and will work in-game. However, we have no way to adjust it! Let's change that.
        // Let's add something to increment our facet's integer value everytime the player joins.
        PlayerEvent.PLAYER_JOIN.register(player -> {
            // Retrieve the facet using the key. Keep in mind, this can be null, since some entities might not have this facet.
            CoolEntityFacet facet = CoolEntityFacet.KEY.get(player);
            if (facet != null)
                facet.someIntegerValue++;
        });
    }
}
```
You're done! You've created your first facet. However, what about the client? Currently, it's acting independently. We don't want that. We wan't the server
to sync the facet to the client. To do this, you first need to go back to your facet class and override the createPacket method:
```java
@Override
public PlayerSendable createPacket(Entity target) {
    // The FacetSyncPacket requires 3 things: the entity holding the facet, the facet key, and the facet instance.
    return new FacetSyncPacket<>(target, KEY, this);
}
```
Now, anytime we want to sync the facet to the client, we can call:
```java
theFacetInstance.sendToClient(theFacetHolder);
```
For example, this would turn our player join code into:
```java
PlayerEvent.PLAYER_JOIN.register(player -> {
    CoolEntityFacet facet = CoolEntityFacet.KEY.get(player);
    if (facet != null) {
        facet.someIntegerValue++;
        facet.sendToClient(player); // Here we send the facet to the client
    }
});
```
However, in many cases, this isn't enough. What about other clients? They might need to know this player's facet data as well, in which case we could use:
```java
theFacetInstance.sendToTrackers(theFacetHolder);
```
This will sync the facet to all players that are tracking the facet holding entity. (Essentially any player with the entity loaded. This is the most you would need.)

But another question arises: what happens if we enter another player's render distance? They too need to know our facet's data. For this, a special class
has been made: the [TrackingUpdateSyncer](https://github.com/RedPxnda/Nucleus/blob/refactor/facet/facet-common/src/main/java/com/redpxnda/nucleus/facet/TrackingUpdateSyncer.java)
Register your facet to it and everything will be handled for you:
```java
TrackingUpdateSyncer.register(theFacetKey);
```
Creating facets for other things like ItemStacks is largely the same. There might be some other methods you have to implement, but they are mostly
self-explanatory or have javadocs explaining them. Rarely will these additional methods requiring something other than the method itself. Some also require
another generic, which is just your class. F.e. `CoolItemStackFacet implements ItemStackFacet<CoolItemStackFacet, NbtCompound>`

Facet classes also feature many useful optional methods. For example, you can listen for when an entity is removed, or when a status effect with your facet ticks.
For ItemStack facets, you may want to manually trigger an nbt update, in which case there is a special utility method for that too: `updateNbtOf`