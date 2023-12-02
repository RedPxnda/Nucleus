package com.redpxnda.nucleus.event;

import dev.architectury.event.CompoundEventResult;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

/**
 * Holds events related to players. More player related events can be found in {@link dev.architectury.event.events.common.PlayerEvent}.
 */
public interface PlayerEvents {
    /**
     * Fired when a player attempts to harvest a block.
     * This event is fired whenever a player attempts to harvest a block in {@link PlayerEntity#canHarvest(BlockState)}.
     * The value in the compound event result represents whether the harvest should be allowed.
     */
    PrioritizedEvent<PlayerHarvestCheck> CAN_PLAYER_HARVEST = PrioritizedEvent.createCompoundEventResult();

    /**
     * Fired when a player attempts to harvest a block.
     * This event is fired whenever a player attempts to harvest a block in
     * {@link AbstractBlock#calcBlockBreakingDelta(BlockState, PlayerEntity, BlockView, BlockPos)}'s usage of {@link PlayerEntity#getBlockBreakingSpeed(BlockState)}.
     * The value in the compound event result represents the new break speed.
     */
    PrioritizedEvent<PlayerBreakSpeed> PLAYER_BREAK_SPEED = PrioritizedEvent.createCompoundEventResult();

    /**
     * Fired when a player's display name is retrieved. (NOTE: the "old" name will include things like team prefixes)
     * This event is fired whenever a player's name is retrieved in {@link PlayerEntity#getDisplayName()}.
     * The value in the compound event result represents the new display name.
     */
    PrioritizedEvent<PlayerDisplayName> PLAYER_DISPLAY_NAME = PrioritizedEvent.createCompoundEventResult();

    /**
     * Fired when a player's tab list name is retrieved. (NOTE: the "old" name will usually be null, unless on forge)
     * This event is fired whenever a player's name is retrieved in {@link ServerPlayerEntity#getPlayerListName()}.
     * The value in the compound event result represents the new display name.
     */
    PrioritizedEvent<PlayerDisplayName> PLAYER_TAB_LIST_NAME = PrioritizedEvent.createCompoundEventResult();


    interface PlayerHarvestCheck {
        CompoundEventResult<Boolean> check(PlayerEntity player, BlockState state, boolean success);
    }
    interface PlayerBreakSpeed {
        CompoundEventResult<Float> get(PlayerEntity player, BlockState state, BlockPos pos, float original);
    }
    interface PlayerDisplayName {
        /**
         * @param old Represents the player's old display name - nullable for tab list names
         */
        CompoundEventResult<Text> get(PlayerEntity player, @Nullable Text old);
    }
}
