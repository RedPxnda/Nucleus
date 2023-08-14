package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.capability.doubles.CapabilityRegistryListener;
import com.redpxnda.nucleus.event.ServerEvents;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.network.clientbound.SyncCapabilitiesJsonPacket;
import dev.architectury.event.events.common.PlayerEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ReloadSyncPackets extends ArrayList<Supplier<SimplePacket>> {
    public static ReloadSyncPackets instance = new ReloadSyncPackets();

    public static void init() {
        ServerEvents.END_DATA_PACK_RELOAD.register(((server, resourceManager, successful) -> instance.forEach(sup -> {
            if (successful) sup.get().send(server.getPlayerList().getPlayers());
        })));
        PlayerEvent.PLAYER_JOIN.register(sp -> instance.forEach(sup -> sup.get().send(sp)));

        //instance.add(SyncParticleShapersPacket::new);
        instance.add(() -> new SyncCapabilitiesJsonPacket(CapabilityRegistryListener.data));
    }
}
