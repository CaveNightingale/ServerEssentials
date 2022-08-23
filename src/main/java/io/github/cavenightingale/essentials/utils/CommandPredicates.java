package io.github.cavenightingale.essentials.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

public class CommandPredicates {
	public static Predicate<ServerCommandSource> player(int permissionLevel) {
		return s -> s.getEntity() instanceof PlayerEntity && s.hasPermissionLevel(permissionLevel);
	}
}
