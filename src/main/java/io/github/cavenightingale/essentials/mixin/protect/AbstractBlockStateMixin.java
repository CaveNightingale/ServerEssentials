package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.SourceChain;
import net.minecraft.block.AbstractBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
	@Inject(method = "randomTick", at = @At("HEAD"))
	private void onRandomTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		SourceChain.push(pos, SourceChain.Comment.RANDOM_TICK);
	}

	@Inject(method = "randomTick", at = @At("RETURN"))
	private void onRandomTickReturn(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		SourceChain.pop(SourceChain.Comment.RANDOM_TICK);
	}

	@Inject(method = "scheduledTick", at = @At("HEAD"))
	private void onScheduledTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		SourceChain.push(pos, SourceChain.Comment.SCHEDULED_TICK);
	}

	@Inject(method = "scheduledTick", at = @At("RETURN"))
	private void onScheduledTickReturn(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		SourceChain.pop(SourceChain.Comment.SCHEDULED_TICK);
	}
}
