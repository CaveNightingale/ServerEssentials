package io.github.cavenightingale.essentials.utils;

import static io.github.cavenightingale.essentials.Essentials.GSON;
import static io.github.cavenightingale.essentials.Essentials.LOGGER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import io.github.cavenightingale.essentials.Essentials;

// this file help user migrate config file from old version of ServerEssentials
public class Updater {
	public static void tryUpdateWarp() {
		File modern = new File("config/Essentials/warps.json");
		File legacy = new File("config/warp.json");
		try (Reader reader = new BufferedReader(new FileReader(legacy, StandardCharsets.UTF_8));
		        Writer writer = new BufferedWriter(new FileWriter(modern, StandardCharsets.UTF_8))) {
			// the class cast here is to make javac call Gson::toJson(Object src, Appendable writer) instead of Gson::toJson(JsonElement jsonElement, Appendable writer)
			Essentials.GSON.toJson(Warps.warps = Essentials.GSON.fromJson(reader, new TypeToken<Warps>(){}.getType()), writer);
		} catch (IOException | JsonParseException ex) {
			LOGGER.debug("Update warp config failed, ignore it.", ex);
			return;
		}
		legacy.delete();
	}

	public static void tryUpdateTranslation() {
		ServerTranslation translation = ServerTranslation.formats = Config.load(ServerTranslation.class, "translation");
		try(Writer writer = new BufferedWriter(new FileWriter("config/Essentials/translation.json", StandardCharsets.UTF_8))) {
			Essentials.GSON.toJson(translation, writer);
		} catch (IOException | JsonParseException ex) {
			LOGGER.debug("Update translation config failed, ignore it.", ex);
		}
	}

	public static void tryUpdateHome(MinecraftServer server) {
		File parent = new File("config/Essentials/homes");
		File[] children = parent.listFiles();
		if(children == null)
			return;
		for(File f : children) {
			UUID uuid;
			try {
				uuid = UUID.fromString(f.getName().replaceAll("\\.json$", ""));
			} catch (RuntimeException ex) {
				LOGGER.debug("Can't update " + f.getName() + " for invalid uuid");
				continue;
			}
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
			SavablePlayerData data = player == null ? SavablePlayerData.load(uuid) : PlayerData.get(player).getSavable();
			try(Reader reader = new BufferedReader(new FileReader(new File(parent, uuid.toString() + ".json")))) {
				data.homes = GSON.fromJson(reader, new TypeToken<HashMap<String, Warps.Warp>>(){}.getType());
			} catch (IOException | JsonParseException e) {
				LOGGER.debug("Can't update " + f.getName() + " for invalid json content");
				continue;
			}
			data.save(uuid);
			f.delete();
		}
		children = parent.listFiles();
		if(children != null && children.length == 0) {
			parent.delete();
		}
	}

	public static void tryUpdateConfig() {
		Config.save(Config.load(Config.class, "config"), "config");
	}
}
