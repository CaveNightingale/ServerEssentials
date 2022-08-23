package io.github.cavenightingale.essentials.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.cavenightingale.essentials.EssentialsCommands;
import io.github.cavenightingale.essentials.utils.CommandNodeWithPermission;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

import static io.github.cavenightingale.essentials.utils.CommandPredicates.player;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

public class EssPermCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("essperm").requires(player(4))
				.then(CommandManager.literal("command")
						.then(CommandManager.literal("set")
								.then(CommandManager.argument("commandNode", StringArgumentType.string())
										.then(CommandManager.argument("permissionNode", StringArgumentType.string()).executes(ctx -> setCommandPermission(ctx, false))
												.then(CommandManager.literal("recursion").executes(ctx -> setCommandPermission(ctx, true))))))
						.then(CommandManager.literal("get")
								.then(CommandManager.argument("commandNode", StringArgumentType.string()).executes(ctx -> getCommandPermission(ctx, false))
										.then(CommandManager.literal("recursion").executes(ctx -> getCommandPermission(ctx, true))))))
				.then(CommandManager.literal("usermod")
						.then(CommandManager.literal("operator")
								.then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
										.then(CommandManager.argument("level", IntegerArgumentType.integer(0, 4))
												.then(CommandManager.argument("byPassPlayerLimit", BoolArgumentType.bool()).executes(EssPermCommand::usermodOperator)))))));
	}

	private static int setCommandPermission(CommandContext<ServerCommandSource> ctx, boolean recursion) {
		String commandPath = StringArgumentType.getString(ctx, "commandNode");
		String permissionNode = StringArgumentType.getString(ctx, "permissionNode");
		CommandNode<?> node = ctx.getSource().getServer().getCommandManager().getDispatcher().getRoot();
		for(String s : commandPath.split("\\.")) {
			node = node.getChild(s);
			if (node == null) {
				ctx.getSource().sendError(formats.esspermCommandNotFoundToSet.format());
				return 0;
			}
		}
		permissionNode = permissionNode.equals("-") ? null : permissionNode;
		updatePermission(node, permissionNode, recursion);
		PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();
		for(ServerPlayerEntity player : playerManager.getPlayerList()) {
			playerManager.sendCommandTree(player);
		}
		EssentialsCommands.saveCommandPermissions(ctx.getSource().getServer());
		ctx.getSource().sendFeedback((recursion ? formats.esspermCommandPermissionSettedRecursion : formats.esspermCommandPermissionSetted).format(commandPath, permissionNode == null ? "原版权限" : permissionNode), true);
		return 1;
	}
	private static void updatePermission(CommandNode<?> node, String perm, boolean recursion) {
		((CommandNodeWithPermission) node).serveressential_setPermission(perm);
		if(recursion) {
			for(CommandNode<?> child : node.getChildren()) {
				updatePermission(child, perm, true);
			}
		}
	}

	private static int getCommandPermission(CommandContext<ServerCommandSource> ctx, boolean recursion) {
		String commandPath = StringArgumentType.getString(ctx, "commandNode");
		CommandNode<?> node = ctx.getSource().getServer().getCommandManager().getDispatcher().getRoot();
		for(String s : commandPath.split("\\.")) {
			node = node.getChild(s);
			if (node == null) {
				ctx.getSource().sendError(formats.esspermCommandNotFoundToGet.format());
				return 0;
			}
		}
		sendPermission(node, commandPath, ctx.getSource(), recursion);
		return 1;
	}

	private static void sendPermission(CommandNode<?> node, String path, ServerCommandSource src, boolean recursion) {
		String permission = ((CommandNodeWithPermission)node).serveressential_getPermission();
		if(permission == null)
			src.sendFeedback(formats.esspermCommandPermissionUseVanilla.format(path), false);
		else
			src.sendFeedback(formats.esspermCommandPermissionUseModded.format(path, permission), false);
		if(recursion) {
			for(CommandNode<?> child : node.getChildren()) {
				sendPermission(child, path + "." + child.getName(), src, true);
			}
		}
	}

	private static int usermodOperator(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		OperatorList opList = ctx.getSource().getServer().getPlayerManager().getOpList();
		Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(ctx, "player");
		int level = IntegerArgumentType.getInteger(ctx, "level");
		boolean byPassPlayerLimit = BoolArgumentType.getBool(ctx, "byPassPlayerLimit");
		profiles.forEach(profile -> {
			opList.add(new OperatorEntry(profile, level, byPassPlayerLimit));
			ServerPlayerEntity entity = ctx.getSource().getServer().getPlayerManager().getPlayer(profile.getId());
			if(entity != null) {
				ctx.getSource().getServer().getPlayerManager().sendCommandTree(entity);
			}
			ctx.getSource().sendFeedback(formats.esspermUsermodOpChanged.format(profile.getName()), true);
		});
		return 1;
	}
}
