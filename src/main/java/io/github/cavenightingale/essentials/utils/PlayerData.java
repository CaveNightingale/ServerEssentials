package io.github.cavenightingale.essentials.utils;

import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerData {
	boolean isServerEssentials_afk();
	void setServerEssentials_afk(boolean serverEssentials_afk);
	SavablePlayerData getSavable();
	static PlayerData get(ServerPlayerEntity entity) {
		return (PlayerData) entity.networkHandler;
	}
}
