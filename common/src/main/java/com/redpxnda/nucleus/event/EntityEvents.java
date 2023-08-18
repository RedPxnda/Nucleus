package com.redpxnda.nucleus.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface EntityEvents {
    PrioritizedEvent<TrackingChange> TRACKING_CHANGE = PrioritizedEvent.createLoop();

    enum TrackingStage {
        STARTED,
        STOPPED
    }
    interface TrackingChange {
        /**
         * Fires when an entity is beginning to be tracked for a player. (After client creation/deletion packets are sent)
         * This is usually when an entity comes in/out of the player's view.
         * <p></p>
         * @param stage whether tracking is starting or ending
         * @param entity the entity being tracked
         * @param player the player receiving the tracking updates
         */
        void onTrackingUpdate(TrackingStage stage, Entity entity, ServerPlayer player);
    }
}
