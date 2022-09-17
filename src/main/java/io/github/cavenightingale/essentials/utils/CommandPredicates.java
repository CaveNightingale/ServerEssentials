package io.github.cavenightingale.essentials.utils;

import java.util.function.Predicate;

import net.minecraft.server.command.ServerCommandSource;

public class CommandPredicates {
	public static Predicate<ServerCommandSource> opLevel(int permissionLevel) {
		return s -> s.hasPermissionLevel(permissionLevel);
	}
}
