package io.github.cavenightingale.essentials.mixin;

import io.github.cavenightingale.essentials.commands.HomeCommand;
import io.github.cavenightingale.essentials.utils.PlayerData;
import io.github.cavenightingale.essentials.utils.Warps;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

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
