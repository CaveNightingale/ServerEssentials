package io.github.cavenightingale.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.cavenightingale.essentials.utils.PlayerData;
import io.github.cavenightingale.essentials.utils.Warps;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static io.github.cavenightingale.essentials.utils.CommandPredicates.opLevel;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

public class HomeCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

		dispatcher.register(CommandManager.literal("home").requires(opLevel(1)).executes(ctx -> home(ctx.getSource(), "spawn"))
				.then(CommandManager.argument("name", StringArgumentType.string()).suggests(HomeCommand::suggestWarps).executes(ctx -> home(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));

		dispatcher.register(CommandManager.literal("back").requires(opLevel(0)).executes(ctx -> home(ctx.getSource(), "back")));

		dispatcher.register(CommandManager.literal("sethome").requires(opLevel(1))
				.then(CommandManager.argument("name", StringArgumentType.string()).executes(ctx -> setHome(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "name"), ctx.getSource(), null))
						.then(CommandManager.argument("description", StringArgumentType.string()).executes(ctx -> setHome(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "name"), ctx.getSource(), StringArgumentType.getString(ctx, "desc"))))));

		dispatcher.register(CommandManager.literal("delhome").requires(opLevel(1)).then(CommandManager.argument("name", StringArgumentType.string()).executes(ctx -> delHome(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "name"), ctx.getSource()))));
	}

	public static int home(ServerCommandSource src, String name) throws CommandSyntaxException {
		ServerPlayerEntity entity = src.getPlayer();
		Warps.Warp warp = PlayerData.get(entity).getSavable().homes.get(name);
		if(warp == null) {
			src.sendError(formats.warpNotFound.format());
			return 0;
		} else {
			warp.teleport(entity);
			return 1;
		}
	}

	// if source == null, no feedback is sent, used else where
	public static int setHome(ServerPlayerEntity entity, String name, @Nullable ServerCommandSource source, @Nullable String desc) {
		Warps.Warp warp = new Warps.Warp(name, entity.getWorld().getRegistryKey().getValue(), entity.getPos(), new FloatFloatImmutablePair(entity.getYaw(), entity.getPitch()), desc);
		PlayerData.get(entity).getSavable().homes.put(warp.name(), warp);
		if(source != null)
			source.sendFeedback(formats.warpSetted.format(warp.name()), false);
		return 1;
	}

	public static int delHome(ServerPlayerEntity entity, String name, @Nullable ServerCommandSource source) {
		Warps.Warp warp = PlayerData.get(entity).getSavable().homes.get(name);
		if(warp == null) {
			if(source != null)
				source.sendError(formats.warpNotFound.format());
			return 0;
		} else {
			PlayerData.get(entity).getSavable().homes.remove(warp.name());
			if(source != null)
				source.sendFeedback(formats.warpDeleted.format(warp.name()), false);
			return 1;
		}
	}

	public static CompletableFuture<Suggestions> suggestWarps(final CommandContext<ServerCommandSource> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
		for(Warps.Warp warp : PlayerData.get(context.getSource().getPlayer()).getSavable().homes.values()) {
			MutableText text = formats.warpLocation.format(warp.world().toString(), new BlockPos(warp.loc()).toShortString()).formatted(Formatting.GRAY);
			if(warp.description() != null)
				text.append(new LiteralText("  " + warp.description()).formatted(Formatting.AQUA));
			builder.suggest("'" + warp.name() + "'", text);
		}
		return builder.buildFuture();
	}
}
