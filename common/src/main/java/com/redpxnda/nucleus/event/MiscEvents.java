package com.redpxnda.nucleus.event;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * CAN_CLIENT_SPRINT: called when the game checks for whether the client player can sprint or not
 * LIVING_JUMP: called when a living entity jumps
 * LIVING_JUMP_POWER: called when a living entity's jump power is read
 */
public interface MiscEvents {
    PrioritizedEvent<SingleInput<PlayerEntity>> CAN_CLIENT_SPRINT = PrioritizedEvent.createEventResult();
    PrioritizedEvent<SingleInput<LivingEntity>> LIVING_JUMP = PrioritizedEvent.createEventResult();
    PrioritizedEvent<CompoundSingleInput<LivingEntity, Float>> LIVING_JUMP_POWER = PrioritizedEvent.createCompoundEventResult();

    interface SingleInput<T> {
        EventResult call(T input);
    }
    interface CompoundSingleInput<T, A> {
        CompoundEventResult<A> call(T input);
    }
}
