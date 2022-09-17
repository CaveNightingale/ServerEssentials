package io.github.cavenightingale.essentials.utils;

import java.util.HashMap;
import java.util.UUID;

public class SavablePlayerData {
	public HashMap<String, Warps.Warp> homes = new HashMap<>();
	public static SavablePlayerData load(UUID uuid) {
		return Config.load(SavablePlayerData.class, uuid.toString(), "/player");
	}

	public void save(UUID uuid) {
		Config.save(this, uuid.toString(), "/player");
	}
}
