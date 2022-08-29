package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.SourceChain;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"))
	private void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		if(context.getPlayer() != null)
			SourceChain.push(new Pair<>(context.getPlayer(), context.getStack()), SourceChain.Comment.SOURCE_ENTITY_PLACE);
	}

	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("RETURN"))
	private void onPlaceReturn(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		if(context.getPlayer() != null)
			SourceChain.pop(SourceChain.Comment.SOURCE_ENTITY_PLACE);
	}
}
