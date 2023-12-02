package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.PlayerEvents;
import dev.architectury.event.CompoundEventResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void nucleus$playerTabListNameEvent(CallbackInfoReturnable<Text> cir) {
        CompoundEventResult<Text> result = PlayerEvents.PLAYER_TAB_LIST_NAME.invoker().get((PlayerEntity) (Object) this, cir.getReturnValue());
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }
}
