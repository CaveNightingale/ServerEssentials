package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.SourceChain;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
	@Shadow @Final protected ServerPlayerEntity player;

	@Inject(method = "tryBreakBlock", at = @At("HEAD"))
	public void onTryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		SourceChain.push(new Pair<>(player, player.getMainHandStack()), SourceChain.Comment.SOURCE_ENTITY_BREAK);
	}

	@Inject(method = "tryBreakBlock", at = @At("RETURN"))
	public void onTryBreakBlockReturn(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		SourceChain.pop(SourceChain.Comment.SOURCE_ENTITY_BREAK);
	}

	@Inject(method = "interactBlock", at = @At("HEAD"))
	public void onInteractBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		SourceChain.push(new Pair<>(player, stack), SourceChain.Comment.SOURCE_ENTITY_INTERACT);
	}

	@Inject(method = "interactBlock", at = @At("RETURN"))
	public void onInteractBlockReturn(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		SourceChain.pop(SourceChain.Comment.SOURCE_ENTITY_INTERACT);
	}
}
