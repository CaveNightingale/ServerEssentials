package io.github.cavenightingale.essentials.mixin;

import io.github.cavenightingale.essentials.Essentials;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {
	@Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
	public boolean getBoolean(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
		return instance.getBoolean(Essentials.gameruleCreeperGriefing);
	}
}
