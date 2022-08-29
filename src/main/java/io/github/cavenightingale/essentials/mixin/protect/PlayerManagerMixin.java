package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.GameEventLogger;
import io.github.cavenightingale.essentials.protect.event.ClientJoinEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(method = "onPlayerConnect", at = @At("RETURN"))
	private void onOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		GameEventLogger.logEvent(new ClientJoinEvent(new Date(), player.getWorld().getRegistryKey().getValue(), new Vec3d(player.getX(), player.getY(), player.getZ()), player.getGameProfile()));
	}
}
