package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.SourceChain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@Shadow @Final private Fluid fluid;

	@Inject(method = "use", at = @At("HEAD"))
	private void onPlaceFluid(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if(fluid != Fluids.EMPTY)
			SourceChain.push(new Pair<>(user, user.getStackInHand(hand)), SourceChain.Comment.SOURCE_ENTITY_PLACE);
		else
			SourceChain.push(new Pair<>(user, user.getStackInHand(hand)), SourceChain.Comment.SOURCE_ENTITY_BREAK);
	}

	@Inject(method = "use", at = @At("RETURN"))
	private void onPlaceFluidReturn(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if(fluid != Fluids.EMPTY)
			SourceChain.pop(SourceChain.Comment.SOURCE_ENTITY_PLACE);
		else
			SourceChain.pop( SourceChain.Comment.SOURCE_ENTITY_BREAK);
	}
}
