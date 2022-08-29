package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.SourceChain;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
	@Inject(method = "tickFluid", at = @At("HEAD"))
	private void onTickFluid(BlockPos pos, Fluid fluid, CallbackInfo ci) {
		SourceChain.push(pos, SourceChain.Comment.FLUID_TICK);
	}

	@Inject(method = "tickFluid", at = @At("RETURN"))
	private void onTickFluidReturn(BlockPos pos, Fluid fluid, CallbackInfo ci) {
		SourceChain.pop(SourceChain.Comment.FLUID_TICK);
	}
}
