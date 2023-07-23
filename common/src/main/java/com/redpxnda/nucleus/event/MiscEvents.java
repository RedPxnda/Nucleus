package com.redpxnda.nucleus.event;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * CAN_CLIENT_SPRINT: called when the game checks for whether the client player can sprint or not
 * LIVING_JUMP: called when a living entity jumps
 * LIVING_JUMP_POWER: called when a living entity's jump power is read
 * MODIFY_CAMERA_SENSITIVITY: called when the client player is attempting to move their camera
 * MOVE_PLAYER_CAMERA: called when a client player attempts to move their camera
 */
public interface MiscEvents {
    Event<SingleInput<Player>> CAN_CLIENT_SPRINT = EventFactory.createEventResult();
    Event<SingleInput<LivingEntity>> LIVING_JUMP = EventFactory.createEventResult();
    Event<CompoundSingleInput<LivingEntity, Float>> LIVING_JUMP_POWER = EventFactory.createCompoundEventResult();

    @Environment(EnvType.CLIENT) Event<ModifyCameraSensitivity> MODIFY_CAMERA_SENSITIVITY = EventFactory.createCompoundEventResult(); // same as below but with modifiable sensitivity
    @Environment(EnvType.CLIENT) Event<SingleInput<Minecraft>> MOVE_PLAYER_CAMERA = EventFactory.createEventResult(); // called when a client player moves their camera

    interface SingleInput<T> {
        EventResult call(T input);
    }
    interface CompoundSingleInput<T, A> {
        CompoundEventResult<A> call(T input);
    }

    @Environment(EnvType.CLIENT)
    interface ModifyCameraSensitivity {
        CompoundEventResult<Double> modify(Minecraft minecraft, double old);
    }
}
