package io.github.cavenightingale.essentials.commands;

import static io.github.cavenightingale.essentials.utils.CommandPredicates.opLevel;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

import java.util.LinkedList;
import java.util.UUID;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Formatting;

import com.mojang.brigadier.CommandDispatcher;

public class TpaCommand {
	record TpaRequest(UUID from, UUID to, long time, boolean reverse) {
		public void accept(MinecraftServer server) {
			ServerPlayerEntity from1 = server.getPlayerManager().getPlayer(from);
			ServerPlayerEntity to1 = server.getPlayerManager().getPlayer(to);
			if(from1 != null && to1 != null) {
				if(reverse) {
					ServerPlayerEntity tmp = from1;
					from1 = to1;
					to1 = tmp;
				}
				from1.teleport(to1.getWorld(), to1.getX(), to1.getY(), to1.getZ(), to1.getYaw(), to1.getPitch());
				from1.sendMessage(formats.tpaAccepted.format().formatted(Formatting.GREEN), false);
				to1.sendMessage(formats.tpaAccepted.format().formatted(Formatting.GREEN), false);
			}
			requests.remove(this);
		}

		public void deny(MinecraftServer server) {
			ServerPlayerEntity from1 = server.getPlayerManager().getPlayer(from);
			ServerPlayerEntity to1 = server.getPlayerManager().getPlayer(to);
			if(from1 != null && to1 != null) {
				from1.sendMessage(formats.tpaDenied.format().formatted(Formatting.RED), false);
				to1.sendMessage(formats.tpaDenied.format().formatted(Formatting.RED), false);
			}
			requests.remove(this);
		}

		public void cancel(MinecraftServer server) {
			ServerPlayerEntity from1 = server.getPlayerManager().getPlayer(from);
			ServerPlayerEntity to1 = server.getPlayerManager().getPlayer(to);
			if(from1 != null && to1 != null) {
				from1.sendMessage(formats.tpaCancelled.format().formatted(Formatting.BLUE), false);
				to1.sendMessage(formats.tpaCancelled.format().formatted(Formatting.BLUE), false);
			}
			requests.remove(this);
		}
	}
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("tpa").requires(opLevel(0)).then(CommandManager.argument("target", EntityArgumentType.player()).executes(
				ctx -> requestTeleport(ctx.getSource().getPlayer(), EntityArgumentType.getPlayer(ctx, "target"), false))));
		dispatcher.register(CommandManager.literal("tpahere").requires(opLevel(0)).then(CommandManager.argument("target", EntityArgumentType.player()).executes(
				ctx -> requestTeleport(ctx.getSource().getPlayer(), EntityArgumentType.getPlayer(ctx, "target"), true))));
		dispatcher.register(CommandManager.literal("tpaccept").requires(opLevel(0)).executes(ctx -> {
			requests.stream().filter(s -> s.to.equals(ctx.getSource().getEntity().getUuid())).toList().forEach(req -> req.accept(ctx.getSource().getServer()));
			return 0;
		}));
		dispatcher.register(CommandManager.literal("tpdeny").requires(opLevel(0)).executes(ctx -> {
			requests.stream().filter(s -> s.to.equals(ctx.getSource().getEntity().getUuid())).toList().forEach(req -> req.deny(ctx.getSource().getServer()));
			return 0;
		}));
		dispatcher.register(CommandManager.literal("tpcancel").requires(opLevel(0)).executes(ctx -> {
			requests.stream().filter(s -> s.from.equals(ctx.getSource().getEntity().getUuid())).toList().forEach(req -> req.cancel(ctx.getSource().getServer()));
			return 0;
		}));
	}

	public static LinkedList<TpaRequest> requests = new LinkedList<>();

	public static void tick(MinecraftServer server) {
		while(!requests.isEmpty() && requests.peek().time < System.currentTimeMillis() - 1000 * 120) {
			requests.peek().deny(server);
		}
	}

	public static int requestTeleport(ServerPlayerEntity from, ServerPlayerEntity to, boolean reserve) {
		from.sendMessage((reserve ? formats.tpaFromMsgReverse :  formats.tpaFromMsg).format(to.getGameProfile().getName()).formatted(Formatting.GREEN, Formatting.ITALIC), false);
		to.sendMessage((reserve ? formats.tpaToMsgReverse : formats.tpaToMsg).format(from.getGameProfile().getName()).formatted(Formatting.GREEN, Formatting.ITALIC), false);
		to.sendMessage(formats.tpaSuggestAccept.format().formatted(Formatting.DARK_GREEN, Formatting.ITALIC).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"))), false);
		to.sendMessage(formats.tpaSuggestDeny.format().formatted(Formatting.DARK_GREEN, Formatting.ITALIC).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"))), false);
		requests.add(new TpaRequest(from.getUuid(), to.getUuid(), System.currentTimeMillis(), reserve));
		return 1;
	}
}
