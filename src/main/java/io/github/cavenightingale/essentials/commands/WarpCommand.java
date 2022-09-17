package io.github.cavenightingale.essentials.commands;

import static io.github.cavenightingale.essentials.utils.CommandPredicates.opLevel;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

import java.util.concurrent.CompletableFuture;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.cavenightingale.essentials.utils.Warps;

import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;

public class WarpCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("warp").requires(opLevel(0)).then(CommandManager.argument("name", StringArgumentType.string()).suggests(WarpCommand::suggestWarps).executes(ctx -> {
			Warps.Warp warp = Warps.warps.get(StringArgumentType.getString(ctx, "name"));
			ServerPlayerEntity entity = ctx.getSource().getPlayer();
			if(warp == null) {
				ctx.getSource().sendError(formats.warpNotFound.format());
				return 0;
			} else {
				warp.teleport(entity);
			}
			return 1;
		})));
		dispatcher.register(CommandManager.literal("setwarp").requires(opLevel(2)).then(CommandManager.argument("name", StringArgumentType.string()).executes(ctx -> setWarp(ctx, null))
				.then(CommandManager.argument("description", StringArgumentType.string()).executes(ctx -> setWarp(ctx, StringArgumentType.getString(ctx, "description"))))));
		dispatcher.register(CommandManager.literal("delwarp").requires(opLevel(2)).then(CommandManager.argument("name", StringArgumentType.string()).suggests(WarpCommand::suggestWarps).executes(ctx -> {
			Warps.Warp warp = Warps.warps.get(StringArgumentType.getString(ctx, "name"));
			if(warp == null) {
				ctx.getSource().sendError(formats.warpNotFound.format());
				return 0;
			} else {
				Warps.warps.remove(warp.name());
				Warps.save();
				ctx.getSource().sendFeedback(formats.warpDeleted.format(warp.name()), true);
			}
			return 1;
		})));
	}

	static int setWarp(CommandContext<ServerCommandSource> ctx, String desc) throws CommandSyntaxException {
		ServerPlayerEntity entity = ctx.getSource().getPlayer();
		Warps.Warp warp = new Warps.Warp(StringArgumentType.getString(ctx, "name"), entity.getWorld().getRegistryKey().getValue(), entity.getPos(), new FloatFloatImmutablePair(entity.getYaw(), entity.getPitch()), desc);
		Warps.warps.put(warp.name(), warp);
		Warps.save();
		ctx.getSource().sendFeedback(formats.warpSetted.format(warp.name()), true);
		return 1;
	}

	public static CompletableFuture<Suggestions> suggestWarps(final CommandContext<ServerCommandSource> context, final SuggestionsBuilder builder) {
		for(Warps.Warp warp : Warps.warps.values()) {
			MutableText text = formats.warpLocation.format(warp.world().toString(), new BlockPos(warp.loc()).toShortString()).formatted(Formatting.GRAY);
			if(warp.description() != null)
				text.append(new LiteralText("  " + warp.description()).formatted(Formatting.AQUA));
			builder.suggest("'" + warp.name() + "'", text);
		}
		return builder.buildFuture();
	}
}
