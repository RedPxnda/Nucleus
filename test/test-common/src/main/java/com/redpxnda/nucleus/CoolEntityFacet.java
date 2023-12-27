package com.redpxnda.nucleus;

import com.redpxnda.nucleus.facet.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.network.clientbound.FacetSyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

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

    // We also want to define some way to sync this to the client. Let's override this method and return the packet type already made for you.
    @Override
    public PlayerSendable createPacket(Entity target) {
        // The FacetSyncPacket requires 3 things: the entity holding the facet, the facet key, and the facet instance.
        return new FacetSyncPacket<>(target, KEY, this);
    }
}
