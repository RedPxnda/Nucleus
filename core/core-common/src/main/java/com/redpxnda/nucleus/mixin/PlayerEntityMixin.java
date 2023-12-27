package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.PlayerEvents;
import dev.architectury.event.CompoundEventResult;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "canHarvest", at = @At("RETURN"), cancellable = true)
    private void nucleus$canPlayerHarvestEvent(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        CompoundEventResult<Boolean> result = PlayerEvents.CAN_PLAYER_HARVEST.invoker().check((PlayerEntity) (Object) this, state, cir.getReturnValue());
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void nucleus$playerDisplayNameEvent(CallbackInfoReturnable<Text> cir) {
        CompoundEventResult<Text> result = PlayerEvents.PLAYER_DISPLAY_NAME.invoker().get((PlayerEntity) (Object) this, cir.getReturnValue());
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }
}
