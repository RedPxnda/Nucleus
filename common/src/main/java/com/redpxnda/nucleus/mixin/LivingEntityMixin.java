package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.MiscEvents;
import com.redpxnda.nucleus.resolving.wrappers.LivingEntityWrapping;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityWrapping {
    @Inject(
            method = "jumpFromGround",
            at = @At("HEAD"),
            cancellable = true)
    private void nucleus$entityJumpEvent(CallbackInfo ci) {
        EventResult result = MiscEvents.LIVING_JUMP.invoker().call((LivingEntity) (Object) this);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @Inject(
            method = "getJumpPower",
            at = @At("HEAD"),
            cancellable = true)
    private void nucleus$entityJumpPowerEvent(CallbackInfoReturnable<Float> cir) {
        CompoundEventResult<Float> result = MiscEvents.LIVING_JUMP_POWER.invoker().call((LivingEntity) (Object) this);
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }
}
