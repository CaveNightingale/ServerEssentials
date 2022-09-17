package io.github.cavenightingale.essentials.protect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import com.mojang.authlib.GameProfile;

import io.github.cavenightingale.essentials.protect.database.DataBaseConnection;
import io.github.cavenightingale.essentials.protect.database.event.LoggedEvent;

public class GameEventLogger {
	private static final DataBaseConnection connection = new DataBaseConnection();
	public static void init() {
		connection.initialize();
	}

	public static void logEvent(LoggedEvent event) {
		connection.write(event);
	}

	public static GameProfile entityProfile(Entity entity) {
		return entity instanceof PlayerEntity player ? player.getGameProfile() : new GameProfile(entity.getUuid(), Registry.ENTITY_TYPE.getId(entity.getType()).toString());
	}

	public static GameProfile entityProfileNullable(Entity entity) {
		return entity == null ? null : entityProfile(entity);
	}

	public static Vec3d entityPos(Entity entity) {
		return new Vec3d(entity.getX(), entity.getY(), entity.getZ());
	}
}
