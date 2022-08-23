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
		File parent = new File("config/Essentials/homes");
		parent.mkdirs();
		try(Reader reader = new BufferedReader(new FileReader(new File(parent, uuid.toString() + ".json"), StandardCharsets.UTF_8))) {
			return Essentials.GSON.fromJson(reader, SavablePlayerData.class);
		} catch (IOException | NullPointerException ex) {
			return new SavablePlayerData();
		}
	}

	public void save(UUID uuid) {
		File parent = new File("config/Essentials/homes");
		parent.mkdirs();
		try(Writer writer = new BufferedWriter(new FileWriter(new File(parent, uuid.toString() + ".json"), StandardCharsets.UTF_8))) {
			Essentials.GSON.toJson(this, writer);
		} catch (IOException ex) {
			LOGGER.error("Failed to save player data", ex);
		}
	}
}
