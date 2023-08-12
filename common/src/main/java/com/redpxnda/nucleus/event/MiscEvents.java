package com.redpxnda.nucleus.event;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * CAN_CLIENT_SPRINT: called when the game checks for whether the client player can sprint or not
 * LIVING_JUMP: called when a living entity jumps
 * LIVING_JUMP_POWER: called when a living entity's jump power is read
 */
public interface MiscEvents {
    Event<SingleInput<Player>> CAN_CLIENT_SPRINT = EventFactory.createEventResult();
    Event<SingleInput<LivingEntity>> LIVING_JUMP = EventFactory.createEventResult();
    Event<CompoundSingleInput<LivingEntity, Float>> LIVING_JUMP_POWER = EventFactory.createCompoundEventResult();

    interface SingleInput<T> {
        EventResult call(T input);
    }
    interface CompoundSingleInput<T, A> {
        CompoundEventResult<A> call(T input);
    }
}
