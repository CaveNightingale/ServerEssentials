package io.github.cavenightingale.essentials.utils;

import io.github.cavenightingale.essentials.Essentials;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static io.github.cavenightingale.essentials.Essentials.LOGGER;

public class SavablePlayerData {
	public HashMap<String, Warps.Warp> homes = new HashMap<>();
	public static SavablePlayerData load(UUID uuid) {
		return Config.load(SavablePlayerData.class, uuid.toString(), "/player");
	}

	public void save(UUID uuid) {
		Config.save(this, uuid.toString(), "/player");
	}
}
