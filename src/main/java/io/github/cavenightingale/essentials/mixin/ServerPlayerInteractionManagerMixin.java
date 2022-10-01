package io.github.cavenightingale.essentials.mixin;

import io.github.cavenightingale.essentials.misc.SeatEntity;
import io.github.cavenightingale.essentials.utils.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
	@Inject(method = "interactBlock", at = @At("RETURN"), cancellable = true)
	private void onOnUse(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		if(!Config.config.sitEnabled)
			return;
		BlockPos pos = hitResult.getBlockPos();
		if(cir.getReturnValue() == ActionResult.PASS && !player.isSneaking() && SeatEntity.isSeat(world.getBlockState(pos)) && SeatEntity.sit(player, pos, -0.5)) {
			cir.setReturnValue(ActionResult.success(true));
		}
	}
}
