package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.InternalHandler;
import io.github.cavenightingale.essentials.protect.SourceChain;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {

	@Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
	public void onSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		InternalHandler.onSetBlockState((ServerWorld) (Object)this, pos, state);
		SourceChain.push(pos, SourceChain.Comment.NEIGHBOUR_BLOCK);
	}

	@Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("RETURN"))
	public void onSetBlockStateReturn(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		SourceChain.pop(SourceChain.Comment.NEIGHBOUR_BLOCK);
	}
}
