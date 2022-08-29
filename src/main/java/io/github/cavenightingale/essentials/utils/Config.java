package io.github.cavenightingale.essentials.utils;

import com.google.gson.JsonParseException;
import io.github.cavenightingale.essentials.Essentials;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

import static io.github.cavenightingale.essentials.Essentials.LOGGER;

public class Config {
	public static Config config = load(Config.class, "config");


	public static <T> T load(Class<T> type, String jsonName, String prefix) {
		File parent = new File("config/Essentials" + prefix);
		parent.mkdirs();
		try(Reader reader = new BufferedReader(new FileReader(new File(parent, jsonName + ".json"), StandardCharsets.UTF_8))) {
			return Objects.requireNonNull(Essentials.GSON.fromJson(reader, type));
		} catch (IOException | NullPointerException | JsonParseException ex) {
			if(!(ex instanceof FileNotFoundException)) // if the file does not exist, we just create file sliently
				LOGGER.error("Cannot load " + jsonName + ", save a new one", ex);
			try {
				if(!(ex instanceof FileNotFoundException))
					Files.move(new File(parent, jsonName + ".json").toPath(), new File(parent, jsonName + ".json.old").toPath());
				try(Writer writer = new BufferedWriter(new FileWriter(new File(parent, jsonName + ".json"), StandardCharsets.UTF_8))) {
					T config1 = type.getConstructor().newInstance();
					Essentials.GSON.toJson(config1, writer);
					return config1;
				}
			} catch (IOException | IllegalAccessException | InstantiationException | NoSuchMethodException |
			         InvocationTargetException e) {
				LOGGER.error("Cannot save a new " + jsonName, ex);
				try {
					return type.getConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
				         InvocationTargetException exc) {
					throw new RuntimeException(exc);
				}
			}
		}
	}

	public static <T> T load(Class<T> type, String jsonName) {
		return load(type, jsonName, "");
	}

	public static void save(Object source, String jsonName, String prefix) {
		File parent = new File("config/Essentials" + prefix);
		parent.mkdirs();
		try(Writer writer = new BufferedWriter(new FileWriter(new File(parent, jsonName + ".json"), StandardCharsets.UTF_8))) {
			Essentials.GSON.toJson(source, writer);
		} catch (IOException ex) {
			LOGGER.error("Failed to save player data", ex);
		}
	}

	public static void save(Object source, String jsonName) {
		save(source, jsonName, "");
	}

	public boolean sitEnabled = true;

	public boolean gameLogEnabled = false;
}
