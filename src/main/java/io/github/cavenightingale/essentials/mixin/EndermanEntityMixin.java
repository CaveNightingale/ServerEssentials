package io.github.cavenightingale.essentials.mixin;

import io.github.cavenightingale.essentials.Essentials;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = {"net/minecraft/entity/mob/EndermanEntity$PlaceBlockGoal", "net/minecraft/entity/mob/EndermanEntity$PickUpBlockGoal"})
public class EndermanEntityMixin {
	@Redirect(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
	public boolean getBoolean(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
		return instance.getBoolean(Essentials.gameruleEndermanGriefing);
	}
}
