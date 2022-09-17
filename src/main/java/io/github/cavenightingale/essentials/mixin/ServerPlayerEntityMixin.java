package io.github.cavenightingale.essentials.mixin;

import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import io.github.cavenightingale.essentials.commands.HomeCommand;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Inject(method = "setSpawnPoint", at = @At("HEAD"))
	private void onSetSpawnPoint(CallbackInfo ci) {
		HomeCommand.setHome((ServerPlayerEntity) (Object) this, "spawn", null, formats.homeSpawn.formatAsString());
	}

	@Inject(method = "onDeath", at = @At("HEAD"))
	private void onOnDeath(CallbackInfo ci) {
		HomeCommand.setHome((ServerPlayerEntity) (Object) this, "back", null, formats.homeDeath.formatAsString());
	}
}
