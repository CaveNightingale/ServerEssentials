package io.github.cavenightingale.essentials.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.GameRules;

import io.github.cavenightingale.essentials.Essentials;

@Mixin(FireballEntity.class)
public class FireballMixin {
	@Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
	public boolean getBoolean(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
		return instance.getBoolean(Essentials.gameruleGhastGriefing);
	}
}
