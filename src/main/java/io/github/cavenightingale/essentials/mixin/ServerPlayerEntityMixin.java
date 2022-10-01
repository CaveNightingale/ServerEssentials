package io.github.cavenightingale.essentials.mixin;

import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

import com.mojang.authlib.GameProfile;
import io.github.cavenightingale.essentials.misc.Skull;
import io.github.cavenightingale.essentials.utils.Config;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import io.github.cavenightingale.essentials.commands.HomeCommand;

import java.util.Collections;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "setSpawnPoint", at = @At("HEAD"))
	private void onSetSpawnPoint(CallbackInfo ci) {
		HomeCommand.setHome((ServerPlayerEntity) (Object) this, "spawn", null, formats.homeSpawn.formatAsString());
	}

	@Inject(method = "onDeath", at = @At("HEAD"))
	private void onOnDeath(DamageSource src, CallbackInfo ci) {
		HomeCommand.setHome((ServerPlayerEntity) (Object) this, "back", null, formats.homeDeath.formatAsString());
		if(Config.config.dropSkullOnDeath && src.getAttacker() instanceof ServerPlayerEntity player && player.interactionManager.isSurvivalLike()) {
			Skull.give(Collections.singleton(getGameProfile()), player, null);
		}
	}
}
