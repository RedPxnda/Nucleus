package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.facet.FacetRegistry;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class NucleusTest {
    public static final String MOD_ID = "nucleus_test";
    
    public static void init() {
        CoolEntityFacet.KEY = FacetRegistry.register(new Identifier("example", "cool_entity_facet"), CoolEntityFacet.class);

        FacetRegistry.ENTITY_FACET_ATTACHMENT.register((entity, attacher) -> {
            if (entity instanceof PlayerEntity)
                attacher.add(CoolEntityFacet.KEY, new CoolEntityFacet(entity));
        });

        PlayerEvent.PLAYER_JOIN.register(player -> {
            CoolEntityFacet facet = CoolEntityFacet.KEY.get(player);
            if (facet != null) {
                facet.someIntegerValue++;
                facet.sendToClient(player);
            }
        });
    }
}
