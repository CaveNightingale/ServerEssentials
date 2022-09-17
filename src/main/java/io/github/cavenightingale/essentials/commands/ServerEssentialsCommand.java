package io.github.cavenightingale.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cavenightingale.essentials.EssentialsCommands;
import io.github.cavenightingale.essentials.utils.Config;
import io.github.cavenightingale.essentials.utils.ServerTranslation;
import io.github.cavenightingale.essentials.utils.Updater;
import io.github.cavenightingale.essentials.utils.Warps;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static io.github.cavenightingale.essentials.utils.CommandPredicates.opLevel;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

public class ServerEssentialsCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("serveressentials").requires(opLevel(4))
				.then(CommandManager.literal("update").executes(ctx -> {
					Updater.tryUpdateHome(ctx.getSource().getServer());
					Updater.tryUpdateTranslation();
					Updater.tryUpdateWarp();
					Updater.tryUpdateConfig();
					EssentialsCommands.resendCommandTree(ctx.getSource().getServer());
					ctx.getSource().sendFeedback(formats.sessUpdated.format(), true);
					return 1;
				}))
				.then(CommandManager.literal("reload").executes(ctx -> {
					formats = Config.load(ServerTranslation.class, "translation");
					Config.config = Config.load(Config.class, "config");
					Warps.warps = Config.load(Warps.class, "warps");
					ctx.getSource().sendFeedback(formats.sessReloaded.format(), true);
					return 1;
				})));
	}
}
