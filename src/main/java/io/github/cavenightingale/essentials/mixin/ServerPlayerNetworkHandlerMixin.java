package io.github.cavenightingale.essentials.mixin;

import io.github.cavenightingale.essentials.utils.PlayerData;
import io.github.cavenightingale.essentials.utils.SavablePlayerData;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

// we mixin here because ServerPlayNetworkHandler does not expire when player respawn
@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayerNetworkHandlerMixin implements PlayerData {
	@Shadow @Final private MinecraftServer server;
	@Shadow public ServerPlayerEntity player;
	boolean serverEssentials_afk = false;
	private SavablePlayerData serverEssentials_savablePlayerData = null;
	@Override
	public boolean isServerEssentials_afk() {
		return serverEssentials_afk;
	}

	@Override
	public void setServerEssentials_afk(boolean serverEssentials_afk) {
		if(this.serverEssentials_afk == serverEssentials_afk)
			return;
		this.serverEssentials_afk = serverEssentials_afk;
		if(serverEssentials_afk) {
			server.getPlayerManager().broadcast(
					formats.miscAfkLeave.format(player.getGameProfile().getName()).formatted(Formatting.GRAY, Formatting.ITALIC), MessageType.CHAT, Util.NIL_UUID);
		} else {
			server.getPlayerManager().broadcast(
					formats.miscAfkBack.format(player.getGameProfile().getName()).formatted(Formatting.GRAY, Formatting.ITALIC), MessageType.CHAT, Util.NIL_UUID);
		}
	}

	@Override
	public SavablePlayerData getSavable() {
		return serverEssentials_savablePlayerData != null ? serverEssentials_savablePlayerData : (serverEssentials_savablePlayerData = SavablePlayerData.load(player.getUuid()));
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	public void onDisconnected(CallbackInfo ci) {
		if(serverEssentials_savablePlayerData != null)
			serverEssentials_savablePlayerData.save(player.getUuid());
	}
}
